package org.ossmeter.platform.vcs.git;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.ossmeter.platform.Constants;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.delta.vcs.AbstractVcsManager;
import org.ossmeter.platform.delta.vcs.VcsChangeType;
import org.ossmeter.platform.delta.vcs.VcsCommit;
import org.ossmeter.platform.delta.vcs.VcsCommitItem;
import org.ossmeter.platform.delta.vcs.VcsRepositoryDelta;
import org.ossmeter.repository.model.GitRepository;
import org.ossmeter.repository.model.VcsRepository;

public class GitManager extends AbstractVcsManager {

	protected String localBaseDirectory = "git/"; // FIXME
	
	@Override
	public boolean appliesTo(VcsRepository repository) {
		return repository instanceof GitRepository;
	}

	@Override
	public String getCurrentRevision(VcsRepository repository) throws Exception {
		Git git = new Git(new FileRepository("tmp-ls")); // FIXME
		Collection<Ref> refs = git.lsRemote().setRemote(repository.getUrl()).call();
		
		String headId = null;
		for (Ref ref : refs) {
			if ("HEAD".equals(ref.getName())) {
				headId = ref.getObjectId().getName();
			}
		}
		return headId;
	}

	/**
	 * To set the startRevision to the first commit, use 'null'
	 * FIXME: This should HANDLE the exception probably..
	 */
	@Override
	public VcsRepositoryDelta getDelta(VcsRepository repository, String startRevision, String endRevision) throws Exception {
		// Clone into local repo
		Git git = getGit((GitRepository)repository);
		
		VcsRepositoryDelta vcsDelta = new VcsRepositoryDelta();
		vcsDelta.setRepository(repository);
		
		Repository repo = git.getRepository();
		RevWalk walk = new RevWalk(repo);
				
		Iterable<RevCommit> logs = git.log().call();
		Iterator<RevCommit> iterator = logs.iterator();

		boolean foundStart = false;
		boolean foundEnd = false;
		
		List<RevCommit> commits = new ArrayList<RevCommit>();
		
		// Reorder the commits (currently they are latest first)
		while (iterator.hasNext()) {
			commits.add(0, walk.parseCommit(iterator.next()));
		}
		
		for (int i = 0; i < commits.size(); i++) {   
			RevCommit commit = commits.get(i);
			RevCommit prevCommit = i - 1 < 0 ? null : commits.get(i-1);
			
		    if (startRevision == null || commit.getId().getName().equals(startRevision)) {
		    	foundStart = true;
		    }
		    if (commit.getId().getName().equals(endRevision)) {
		    	foundEnd = true;
		    }
		    
		    VcsCommit vcsCommit = null;
		    if (foundStart) {
			    // Setup the meta data for the commit
		    	vcsCommit = new VcsCommit();
			    vcsCommit.setRevision(commit.getId().getName());
			    vcsCommit.setMessage(commit.getFullMessage());
			    vcsCommit.setAuthor(commit.getAuthorIdent().getName());
			    vcsCommit.setDelta(vcsDelta);
			    vcsDelta.getCommits().add(vcsCommit);
			    
			    if (prevCommit != null) {
			    	// Do a diff against the succeeding commit
					ObjectId thisCommitId = repo.resolve(commit.getId().getName() + "^{tree}");
					ObjectId prevCommitId = repo.resolve(prevCommit.getId().getName() + "^{tree}");
			    	
					ObjectReader reader = repo.newObjectReader();
					
					CanonicalTreeParser currentTreeIter = new CanonicalTreeParser();
					currentTreeIter.reset(reader, thisCommitId);
					
					CanonicalTreeParser prevTreeIter = new CanonicalTreeParser();
					prevTreeIter.reset(reader, prevCommitId);
					
					List<DiffEntry> diffs= git.diff()
					                        .setNewTree(currentTreeIter)
					                        .setOldTree(prevTreeIter)
					                        .call();
					
					for (DiffEntry diff: diffs) {
						VcsChangeType change = convertChangeType(diff.getChangeType());
						if (change == null) continue;
						VcsCommitItem item = new VcsCommitItem();
			    		item.setPath(diff.getNewPath());
			    		item.setChangeType(change);
			    		item.setCommit(vcsCommit);
			    		vcsCommit.getItems().add(item);
					}
			    } else {
			    	// First commit: everything is ADDED
			    	vcsCommit = new VcsCommit();
			    	TreeWalk treeWalk = new TreeWalk(repo);
				    treeWalk.addTree(commit.getTree());
				    while(treeWalk.next()) {
				    	if (treeWalk.isSubtree()) {
				    		treeWalk.enterSubtree();
				    	} else {
				    		VcsCommitItem item = new VcsCommitItem();
				    		item.setPath(treeWalk.getPathString());
				    		item.setChangeType(VcsChangeType.ADDED);
				    		item.setCommit(vcsCommit);
				    		vcsCommit.getItems().add(item);
				    	}
				    }
			    }
		    }
		    
		    if (foundEnd) {
		    	break;
		    }
		}
		
		return vcsDelta;
	}

	private VcsChangeType convertChangeType(ChangeType changeType) {
		switch(changeType) {
			case ADD:
				return VcsChangeType.ADDED;
			case COPY:
				return null;
			case DELETE:
				return VcsChangeType.DELETED;
			case MODIFY:
				return VcsChangeType.UPDATED;
			case RENAME:
				return VcsChangeType.UPDATED;
		}
		return null;
	}

	@Override
	public String getContents(VcsCommitItem item) throws Exception {
		String contents = "";
		String projectName = makeSafe(item.getCommit().getDelta().getRepository().getUrl());
		File file = new File(localBaseDirectory + projectName + "/" + item.getPath());

		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()) {
			contents += scanner.nextLine() + Constants.NEW_LINE; 
		}
		scanner.close();
		
		return contents;
	}

	protected String makeSafe(String str) {
		return str.replace("/","").replace(":","");
	}

	@Override
	public String getFirstRevision(VcsRepository repository) throws Exception {
		Git git = getGit((GitRepository)repository);
		
		Repository repo = git.getRepository();
		RevWalk walk = new RevWalk(repo);
		
		Iterator<RevCommit> iterator = git.log().call().iterator();
		walk.parseCommit(iterator.next());
		
		String revision = null;
		
		// The commits are ordered latest first, so we want the last one.
		while(iterator.hasNext()) { 
			RevCommit commit = iterator.next();
			if (!iterator.hasNext()) {
				revision = commit.getId().getName();
			}
		}
		
		return revision;
	}

	@Override
	public int compareVersions(VcsRepository repository, String versionOne, String versionTwo) throws Exception {
		Git git = getGit((GitRepository)repository);
		
		Repository repo = git.getRepository();
		RevWalk walk = new RevWalk(repo);
		
		Iterator<RevCommit> iterator = git.log().call().iterator();
		walk.parseCommit(iterator.next());
		
		List<String> revisions = new ArrayList<String>();
		
		// The commits are ordered latest first, so we want the last one.
		while(iterator.hasNext()) { 
			RevCommit commit = iterator.next();
			revisions.add(commit.getId().getName());
		}
		Integer oneIndex = revisions.indexOf(versionOne);
		Integer twoIndex = revisions.indexOf(versionTwo);
		
		System.out.println(oneIndex);
		System.out.println(twoIndex);
		System.out.println(revisions);
		
		// Because the revision list is reversed, we compare two to one instead of the other way around
		return twoIndex.compareTo(oneIndex);
	}

	@Override
	public String[] getRevisionsForDate(VcsRepository repository, Date date) throws Exception {
		long epoch = date.toJavaDate().getTime();
		
		List<String> revisions = new ArrayList<String>();
		
		Git git = getGit((GitRepository)repository);
		
		Repository repo = git.getRepository();
		RevWalk walk = new RevWalk(repo);
		
		Iterator<RevCommit> iterator = git.log().call().iterator();
		walk.parseCommit(iterator.next());
		
		boolean foundDate = false;
		while(iterator.hasNext()) { 
			RevCommit commit = iterator.next();
			
			System.out.println(Long.valueOf(commit.getCommitTime())*1000 + " == " + epoch); 
			if (new Date(Long.valueOf(commit.getCommitTime())*1000).toString().equals(date.toString())) {
				foundDate = true;
				revisions.add(commit.getId().getName());
			} else if (foundDate) {
				break;
			}
		}
		
		return revisions.toArray(new String[revisions.size()]);
	}

	@Override
	public Date getDateForRevision(VcsRepository repository, String revision) throws Exception {
		Git git = getGit((GitRepository)repository);
		
		Repository repo = git.getRepository();
		RevWalk walk = new RevWalk(repo);
		
		Iterator<RevCommit> iterator = git.log().call().iterator();
		walk.parseCommit(iterator.next());
		
		Date date = null;
		while (iterator.hasNext()) {
			RevCommit commit = iterator.next();
			if (commit.getId().getName().equals(revision)) {
				date = new Date(Long.valueOf(commit.getCommitTime())*1000); 
			}
		}
		
		return date;
	}
	

	protected Git getGit(GitRepository repository) throws Exception {
		String localPath = localBaseDirectory + makeSafe(repository.getUrl()); // FIXME local stora1ge

		Git git;
		File gitDir = new File(localPath);
		if (gitDir.exists()) {
			git = new Git(new FileRepository(localPath + "/.git"));
			git.pull().call(); 
		} else {
			git = Git.cloneRepository() 
					.setURI(repository.getUrl())
					.setDirectory(gitDir)
					.call();
		}
		return git;
	}
	
}