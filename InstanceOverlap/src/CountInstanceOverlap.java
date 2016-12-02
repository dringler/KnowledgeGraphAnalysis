import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;


public class CountInstanceOverlap {

	public static void main(String[] args) {
		
		ClassMapping cM = new ClassMapping();
		ArrayList<String> classNames = getClassNames();
		
		// COUNT SAME AS LINKS
		// PARAMETERS		
		boolean d2y = true;
		boolean d2o = true;
		boolean y2d = true;
		boolean o2d = true;
		CountSameAs same = new CountSameAs();
		//same.run(classNames, cM, d2y, d2o, y2d, o2d);
		
		// COUNT INSTANCE OVERLAP USING STRING SIMILARITY MEASURES
		
		CountStringSimilarity stringSim = new CountStringSimilarity();
		//configure log4j for secondstring library
		org.apache.log4j.BasicConfigurator.configure();
		LogManager.getRootLogger().setLevel(Level.OFF); //set console logger off
		
		StringMeasures stringMeasures = new StringMeasures();
		stringSim.run(classNames, cM, stringMeasures);
		

	}
	
	private static ArrayList<String> getClassNames() {
		ArrayList<String> classNames = new ArrayList<String>();
		classNames.addAll(Arrays.asList(
							//PERSON
								/*"Agent",
								"Person",
								"Politician",
								"Athlete",
								"Actor",
							//ORGANIZATION
								"GovernmentOrganization",
								"Company",
								"PoliticalParty",
							//PLACE
								"Place",
								"PopulatedPlace",
								"City_Village_Town",
								"Country",
							//ART
								"Work",
								"MusicalWork",
								"Album",
								"Song",
								"Single",
								"Movie",
								"Book",
							//EVENT	
								"Event",
								"MilitaryConflict",
								"SocietalEvent",
								"SportsEvent",
							//TRANSPORT
								"Vehicle",
								"Automobile",
								"Ship",
								"Spacecraft",
							//OTHER
								"ChemicalElement_Substance",*/
								"CelestialBody_Object",
								"Planet"
							));
		return classNames;
	}
	

}
