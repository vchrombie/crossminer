package org.ossmeter.platform.app.example;

import com.mongodb.Mongo;

public class ResetApp {
	
	public static void main(String[] args) throws Exception {
		
		Mongo mongo = new Mongo();
		mongo.dropDatabase("ossmeter");
		mongo.dropDatabase("pdb_values");
		mongo.dropDatabase("pongo");
		mongo.dropDatabase("fedora");
		mongo.dropDatabase("epsilon");
		mongo.dropDatabase("Epsilon");
		mongo.dropDatabase("hamcrest");
		mongo.dropDatabase("jMonkeyEngine");
		mongo.dropDatabase("thunderbird");
		mongo.dropDatabase("fedora");
		mongo.dropDatabase("saf");
		mongo.dropDatabase("mojambo-grit");
		mongo.dropDatabase("emf");
		mongo.dropDatabase("xText");
		mongo.dropDatabase("toolsEmf");
		mongo.dropDatabase("eclipsePlatform");
		mongo.dropDatabase("pdb_values");
		mongo.dropDatabase("pongo");
		mongo.dropDatabase("modelinggmt");
		mongo.dropDatabase("eclipse");
		mongo.dropDatabase("modelinggmtamw");
		mongo.dropDatabase("Log4J");
		mongo.dropDatabase("BIRT");
		mongo.dropDatabase("SiriusForum");
	}
	
}
