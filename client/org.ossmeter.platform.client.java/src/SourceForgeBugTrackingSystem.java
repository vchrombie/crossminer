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
	@Type(value = SourceForgeBugTrackingSystem.class, name="SourceForgeBugTrackingSystem"), })
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceForgeBugTrackingSystem extends BugTrackingSystem {

	protected List<BugTS> bugsTS;
	
	
	public List<BugTS> getBugsTS() {
		return bugsTS;
	}
}
