package org.ossmeter.metricprovider.historic.newsgroups.responsetime;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ossmeter.metricprovider.historic.newsgroups.responsetime.model.NewsgroupsResponseTimeHistoricMetric;
import org.ossmeter.metricprovider.trans.newsgroups.threadsrequestsreplies.ThreadsRequestsRepliesTransMetricProvider;
import org.ossmeter.metricprovider.trans.newsgroups.threadsrequestsreplies.model.NewsgroupsThreadsRequestsRepliesTransMetric;
import org.ossmeter.metricprovider.trans.newsgroups.threadsrequestsreplies.model.ThreadStatistics;
import org.ossmeter.platform.AbstractHistoricalMetricProvider;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.communicationchannel.nntp.NntpUtil;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;

import com.googlecode.pongo.runtime.Pongo;

public class ResponseTimeHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.historic.newsgroups.responsetime";

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
//		final long startTime = System.currentTimeMillis();

		if (uses.size()!=1) {
			System.err.println("Metric: dailyavgresponsetimepernewsgroup failed to retrieve " + 
								"the transient metric it needs!");
			System.exit(-1);
		}

		NewsgroupsThreadsRequestsRepliesTransMetric usedThreads = 
				((ThreadsRequestsRepliesTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));

		Date currentDate = context.getDate();

		long sumOfDurations = 0,
			 cumulativeSumOfDurations = 0;
		int threadsConsidered = 0,
			cumulativeThreadsConsidered = 0;
		String lastUrl_name = "";
		
		for (ThreadStatistics thread: usedThreads.getThreads()) {
			lastUrl_name = thread.getUrl_name();
			if (thread.getAnswered()) {
				cumulativeSumOfDurations += thread.getResponseDurationSec();
				cumulativeThreadsConsidered++;
				java.util.Date responseDate = NntpUtil.parseDate(thread.getResponseDate());
				if (currentDate.compareTo(responseDate)==0) {
					sumOfDurations += thread.getResponseDurationSec();
					threadsConsidered++;
				}
			}
		}
		
		NewsgroupsResponseTimeHistoricMetric dailyAverageThreadResponseTime = new NewsgroupsResponseTimeHistoricMetric();

		if ( (threadsConsidered>0) || (cumulativeThreadsConsidered>0) ) {
			
			dailyAverageThreadResponseTime.setUrl_name(lastUrl_name);
			dailyAverageThreadResponseTime.setThreadsConsidered(threadsConsidered);
			dailyAverageThreadResponseTime.setCumulativeThreadsConsidered(cumulativeThreadsConsidered);
			
			String avgResponseTime = computeAverageDuration(sumOfDurations, threadsConsidered);
			dailyAverageThreadResponseTime.setAvgResponseTime(avgResponseTime);
			
			String cumulativeAvgResponseTime = computeAverageDuration(cumulativeSumOfDurations, cumulativeThreadsConsidered);
			dailyAverageThreadResponseTime.setCumulativeAvgResponseTime(cumulativeAvgResponseTime);
		}

//		System.err.println(time(System.currentTimeMillis() - startTime) + "\tdaily_new");
		return dailyAverageThreadResponseTime;
	}

	private static final long SECONDS_DAY = 24 * 60 * 60;

	private String computeAverageDuration(long sumOfDurations, int threads) {
		String formatted = null;
		if (threads>0) {
			long avgDuration = sumOfDurations/threads;
			int days = (int) (avgDuration / SECONDS_DAY);
			long lessThanDay = (avgDuration % SECONDS_DAY);
			formatted = days + ":" + 
					DurationFormatUtils.formatDuration(lessThanDay*1000, "HH:mm:ss:SS");
		} else {
			formatted = 0 + ":" + 
					DurationFormatUtils.formatDuration(0, "HH:mm:ss:SS");
		}
		return formatted;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ThreadsRequestsRepliesTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "avgresponsetimepernewsgroup";
	}

	@Override
	public String getFriendlyName() {
		return "Average Thread Response Time Per Day Per Newsgroup";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the average time in which the community " +
			   "responds to open threads per day for each newsgroup separately." + 
			   "Format: dd:HH:mm:ss:SS, where dd=days, HH:hours, mm=minutes, ss:seconds, SS=milliseconds.";
	}
}