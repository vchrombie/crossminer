package org.ossmeter.metricprovider.rascal.trans.model;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class URIMeasurement extends Measurement {
	
	
	
	public URIMeasurement() { 
		super();
		super.setSuperTypes("org.ossmeter.metricprovider.rascal.trans.model.Measurement");
		URI.setOwningType("org.ossmeter.metricprovider.rascal.trans.model.URIMeasurement");
		VALUE.setOwningType("org.ossmeter.metricprovider.rascal.trans.model.URIMeasurement");
	}
	
	public static StringQueryProducer URI = new StringQueryProducer("uri"); 
	public static StringQueryProducer VALUE = new StringQueryProducer("value"); 
	
	
	public String getValue() {
		return parseString(dbObject.get("value")+"", "");
	}
	
	public URIMeasurement setValue(String value) {
		dbObject.put("value", value);
		notifyChanged();
		return this;
	}
	
	
	
	
}