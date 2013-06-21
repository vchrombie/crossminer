package org.ossmeter.metricprovider.numberofbugzillacomments;

import java.util.List;

import org.ossmeter.metricprovider.numberofbugzillacomments.model.BugzillaData;
import org.ossmeter.metricprovider.numberofbugzillacomments.model.Nobc;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.ITransientMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemProjectDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.PlatformBugTrackingSystemManager;
import org.ossmeter.repository.model.BugTrackingSystem;
import org.ossmeter.repository.model.Bugzilla;
import org.ossmeter.repository.model.Project;

import com.mongodb.DB;

public class NobcMetricProvider implements ITransientMetricProvider<Nobc>{

	protected PlatformBugTrackingSystemManager platformBugTrackingSystemManager;

	@Override
	public String getIdentifier() {
		return NobcMetricProvider.class.getCanonicalName();
	}

	@Override
	public boolean appliesTo(Project project) {
		for (BugTrackingSystem bugTrackingSystem: project.getBugTrackingSystems()) {
			if (bugTrackingSystem instanceof Bugzilla) return true;
		}
		return false;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		// DO NOTHING -- we don't use anything
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return null;
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.platformBugTrackingSystemManager = context.getPlatformBugTrackingSystemManager();
	}

	@Override
	public Nobc adapt(DB db) {
		return new Nobc(db);
	}

	@Override
	public void measure(Project project, ProjectDelta projectDelta, Nobc db) {
		BugTrackingSystemProjectDelta delta = projectDelta.getBugTrackingSystemDelta();
		
		for (BugTrackingSystemDelta bugTrackingSystemDelta : delta.getBugTrackingSystemDeltas()) {
			BugTrackingSystem bugTrackingSystem = bugTrackingSystemDelta.getBugTrackingSystem();
			if (!(bugTrackingSystem instanceof Bugzilla)) continue;
			Bugzilla bugzilla = (Bugzilla) bugTrackingSystem;
			String url_prod_comp = 
					bugzilla.getUrl()+"#"+bugzilla.getProduct()+"#"+bugzilla.getComponent();
			BugzillaData bugzillaData = db.getBugzillas().findOneByUrl_prod_comp(url_prod_comp);
			if (bugzillaData == null) {
				bugzillaData = new BugzillaData();
				bugzillaData.setUrl_prod_comp(url_prod_comp);
				db.getBugzillas().add(bugzillaData);
			} 
			bugzillaData.setNumberOfComments(bugTrackingSystemDelta.getComments().size());
//			System.out.println("bugTrackingSystemDelta.getComments().size(): " + 
//								bugTrackingSystemDelta.getComments().size());
			db.sync();
		}
	}

}