//package org.ossmeter.repository.model.vcs.cvs;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
	include=JsonTypeInfo.As.PROPERTY,
	property = "_type")
@JsonSubTypes({
	@Type(value = CvsRepository.class, name="CvsRepository"), })
@JsonIgnoreProperties(ignoreUnknown = true)
public class CvsRepository extends VcsRepository {

	protected String browse;
	protected String username;
	protected String password;
	protected String path;
	
	public String getBrowse() {
		return browse;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getPath() {
		return path;
	}
	
}
