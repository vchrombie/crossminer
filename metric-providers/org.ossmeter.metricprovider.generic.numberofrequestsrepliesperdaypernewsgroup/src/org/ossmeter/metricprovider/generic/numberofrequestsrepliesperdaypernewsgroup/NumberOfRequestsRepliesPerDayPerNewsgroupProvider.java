package org.ossmeter.metricprovider.generic.numberofrequestsrepliesperdaypernewsgroup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ossmeter.metricprovider.generic.numberofrequestsrepliesperdaypernewsgroup.model.DailyNewsgroupData;
import org.ossmeter.metricprovider.generic.numberofrequestsrepliesperdaypernewsgroup.model.DailyNorr;
import org.ossmeter.metricprovider.requestreplyclassification.RequestReplyClassificationMetricProvider;
import org.ossmeter.metricprovider.requestreplyclassification.model.NewsgroupArticlesData;
import org.ossmeter.metricprovider.requestreplyclassification.model.Rrc;
import org.ossmeter.platform.IHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;
import org.ossmeter.repository.model.Project;

import com.googlecode.pongo.runtime.Pongo;

public class NumberOfRequestsRepliesPerDayPerNewsgroupProvider implements IHistoricalMetricProvider{
	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.generic.numberofrequestsrepliesperdaypernewsgroup";

	protected MetricProviderContext context;
	
	/**
	 * List of MPs that are used by this MP. These are MPs who have specified that 
	 * they 'provide' data for this MP.
	 */
	protected List<IMetricProvider> uses;
	
	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}
	
	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
		}
		return false;
	}

	@Override
	public Pongo measure(Project project) {

		DailyNorr dailyNorr = new DailyNorr();
		Set<String> newsgroupUrls = new HashSet<String>();
		Map<String, Integer> requests = new HashMap<String, Integer>(), 
							  replies = new HashMap<String, Integer>();
		for (IMetricProvider used : uses) {
			Rrc usedRrc = ((RequestReplyClassificationMetricProvider)used).
							adapt(context.getProjectDB(project));
			for (NewsgroupArticlesData naData: usedRrc.getNewsgroupArticles()) {
				Map<String, Integer> rr = null;
				if (naData.getClassificationResult().equals("Request")) 
					rr = requests;
				else if (naData.getClassificationResult().equals("Reply")) 
					rr = replies;
				if (rr!=null) {
					newsgroupUrls.add(naData.getUrl());
					if (rr.containsKey(naData.getUrl()))
						rr.put(naData.getUrl(), rr.get(naData.getUrl()) + 1);
					else
						rr.put(naData.getUrl(), 1);
				} else {
					System.err.println("Classification result ( " + 
							naData.getClassificationResult() + 
									" ) should be either Request or Reply!");
				}
			}
		}
		for (String newsgroupUrl: newsgroupUrls) {
			DailyNewsgroupData dailyNewsgroupData = new DailyNewsgroupData();
			dailyNewsgroupData.setUrl_name(newsgroupUrl);
			if (requests.containsKey(newsgroupUrl))
				dailyNewsgroupData.setNumberOfRequests(requests.get(newsgroupUrl));
			else
				dailyNewsgroupData.setNumberOfRequests(0);
			if (replies.containsKey(newsgroupUrl))
				dailyNewsgroupData.setNumberOfReplies(replies.get(newsgroupUrl));
			else
				dailyNewsgroupData.setNumberOfReplies(0);
			dailyNorr.getNewsgroups().add(dailyNewsgroupData);
		}
		return dailyNorr;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(RequestReplyClassificationMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "nrpdpn";
	}

	@Override
	public String getFriendlyName() {
		return "Number Of Requests and Replies Per Day Per Newsgroup";
	}

	@Override
	public String getSummaryInformation() {
		return "This class computes the number of request and reply newsgroup articles " +
				"per day for each newsgroup separately.";
	}

}