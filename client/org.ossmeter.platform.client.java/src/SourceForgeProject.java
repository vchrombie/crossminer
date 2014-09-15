//package org.ossmeter.repository.model.sourceforge;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
	include=JsonTypeInfo.As.PROPERTY,
	property = "_type")
@JsonSubTypes({
	@Type(value = SourceForgeProject.class, name="SourceForgeProject"), })
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceForgeProject extends Project {

	protected List<OS> os;
	protected List<Topic> topics;
	protected List<ProgrammingLanguage> programminLanguages;
	protected List<Audience> audiences;
	protected List<Environment> environments;
	protected List<Category> categories;
	protected List<FeatureRequest> featureRequests;
	protected List<SupportRequest> supportRequests;
	protected List<Patch> patches;
	protected List<SourceForgeBugTrackingSystem> bts;
	protected List<Discussion> discussion;
	protected List<Bug> bugs;
	protected String created;
	protected int projectId;
	protected int _private;
	protected String shortDesc;
	protected float percentile;
	protected int ranking;
	protected String downloadPage;
	protected String supportPage;
	protected String summaryPage;
	protected String homePage;
	protected MailingList mailingList;
	protected Donation donation;
	
	public String getCreated() {
		return created;
	}
	public int getProjectId() {
		return projectId;
	}
	public int get_private() {
		return _private;
	}
	public String getShortDesc() {
		return shortDesc;
	}
	public float getPercentile() {
		return percentile;
	}
	public int getRanking() {
		return ranking;
	}
	public String getDownloadPage() {
		return downloadPage;
	}
	public String getSupportPage() {
		return supportPage;
	}
	public String getSummaryPage() {
		return summaryPage;
	}
	public String getHomePage() {
		return homePage;
	}
	
	public List<OS> getOs() {
		return os;
	}
	public List<Topic> getTopics() {
		return topics;
	}
	public List<ProgrammingLanguage> getProgramminLanguages() {
		return programminLanguages;
	}
	public List<Audience> getAudiences() {
		return audiences;
	}
	public List<Environment> getEnvironments() {
		return environments;
	}
	public List<Category> getCategories() {
		return categories;
	}
	public List<FeatureRequest> getFeatureRequests() {
		return featureRequests;
	}
	public List<SupportRequest> getSupportRequests() {
		return supportRequests;
	}
	public List<Patch> getPatches() {
		return patches;
	}
	public List<SourceForgeBugTrackingSystem> getBts() {
		return bts;
	}
	public List<Discussion> getDiscussion() {
		return discussion;
	}
	public List<Bug> getBugs() {
		return bugs;
	}
}
