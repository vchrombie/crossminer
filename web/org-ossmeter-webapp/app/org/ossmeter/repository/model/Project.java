package org.ossmeter.repository.model;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import org.ossmeter.repository.model.redmine.*;
import org.ossmeter.repository.model.vcs.svn.*;
import org.ossmeter.repository.model.cc.forum.*;
import org.ossmeter.repository.model.bts.bugzilla.*;
import org.ossmeter.repository.model.cc.nntp.*;
import org.ossmeter.repository.model.vcs.cvs.*;
import org.ossmeter.repository.model.eclipse.*;
import org.ossmeter.repository.model.googlecode.*;
import org.ossmeter.repository.model.vcs.git.*;
import org.ossmeter.repository.model.sourceforge.*;
import org.ossmeter.repository.model.github.*;
import org.ossmeter.repository.model.*;
import org.ossmeter.repository.model.cc.wiki.*;
import org.ossmeter.repository.model.metrics.*;
import org.ossmeter.platform.factoids.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
	include=JsonTypeInfo.As.PROPERTY,
	property = "_type")
@JsonSubTypes({
	@Type(value = Project.class, name="org.ossmeter.repository.model.Project"), 	@Type(value = org.ossmeter.repository.model.eclipse.EclipseProject.class, name="org.ossmeter.repository.model.eclipse.EclipseProject"),
	@Type(value = org.ossmeter.repository.model.github.GitHubRepository.class, name="org.ossmeter.repository.model.github.GitHubRepository"),
	@Type(value = org.ossmeter.repository.model.googlecode.GoogleCodeProject.class, name="org.ossmeter.repository.model.googlecode.GoogleCodeProject"),
	@Type(value = org.ossmeter.repository.model.redmine.RedmineProject.class, name="org.ossmeter.repository.model.redmine.RedmineProject"),
	@Type(value = org.ossmeter.repository.model.sourceforge.SourceForgeProject.class, name="org.ossmeter.repository.model.sourceforge.SourceForgeProject"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends NamedElement {

	protected List<VcsRepository> vcsRepositories = new ArrayList<VcsRepository>();
	protected List<CommunicationChannel> communicationChannels = new ArrayList<CommunicationChannel>();
	protected List<BugTrackingSystem> bugTrackingSystems = new ArrayList<BugTrackingSystem>();
	protected List<Person> persons;
	protected List<License> licenses;
	protected List<MetricProvider> metricProviderData;
	protected List<Company> companies;
	protected String shortName;
	protected String description;
	protected int year;
	protected boolean active;
	protected boolean analysed;
	protected String lastExecuted;
	protected Project parent;
	protected String homePage;
	
	public String getShortName() {
		return shortName;
	}
	public String getDescription() {
		return description;
	}
	public int getYear() {
		return year;
	}
	public boolean getActive() {
		return active;
	}
	public boolean getAnalysed() {
		return analysed;
	}
	public String getLastExecuted() {
		return lastExecuted;
	}
	public String getHomePage() {
		return homePage;
	}
	
	public List<VcsRepository> getVcsRepositories() {
		return vcsRepositories;
	}
	public List<CommunicationChannel> getCommunicationChannels() {
		return communicationChannels;
	}
	public List<BugTrackingSystem> getBugTrackingSystems() {
		return bugTrackingSystems;
	}
	public List<Person> getPersons() {
		return persons;
	}
	public List<License> getLicenses() {
		return licenses;
	}
	public List<MetricProvider> getMetricProviderData() {
		return metricProviderData;
	}
	public List<Company> getCompanies() {
		return companies;
	}

	public void setShortName(String sName) {
		shortName = sName;
	}
	
	public void setHomePage(String hPage) {
		homePage = hPage;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
}
