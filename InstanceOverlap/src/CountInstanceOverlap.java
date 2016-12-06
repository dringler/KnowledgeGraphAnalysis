import java.util.ArrayList;
import java.util.Arrays;

public class CountInstanceOverlap {

	public static void main(String[] args) {
		
		ClassMapping cM = new ClassMapping();
		ArrayList<String> classNames = getClassNames();
		
	// COUNT SAME AS LINKS
		// PARAMETERS		
		/*boolean d2y = true;
		boolean d2o = true;
		boolean y2d = true;
		boolean o2d = true;
		CountSameAs same = new CountSameAs();
		//same.run(classNames, cM, d2y, d2o, y2d, o2d);
		*/
	// COUNT INSTANCE OVERLAP USING STRING SIMILARITY MEASURES
		boolean useSamples = false;
		// PARAMETERS: string similarity measures and thresholds
		boolean exactMatch = true;
		boolean jaccard = false;
		double jaccardT = 0.5;
		boolean jaro = false;
		double jaroT = 0.9;
		boolean scaledLevenstein = true;
		double scaledLevensteinT = 0.8;
		boolean tfidf = false;
		double tfidfT = 0.7;
		boolean jaroWinkler = true;
		double jaroWinklerT = 0.8;
		boolean softTfidf = true;
		double softTfidfT = 0.8;
		boolean internalSoftTfidf = true;
		String internalSoftTfidfS = "jaroWinkler"; //"jaroWinkler", "jaccard", or "scaledLevenstein"
		double internalSoftTfidfT = 0.9;
		
		StringMeasures stringMeasures = new StringMeasures(exactMatch,
				jaccard, jaccardT, 
				jaro, jaroT, 
				scaledLevenstein, scaledLevensteinT, 
				tfidf, tfidfT, 
				jaroWinkler, jaroWinklerT,
				softTfidf, softTfidfT, internalSoftTfidf, internalSoftTfidfS, internalSoftTfidfT);
		
		//configure log4j for secondstring library
		org.apache.log4j.BasicConfigurator.configure();
		//LogManager.getRootLogger().setLevel(Level.OFF); //set console logger off
		
		CountStringSimilarity stringSim = new CountStringSimilarity();
		stringSim.run(classNames, cM, stringMeasures, useSamples);
		

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
								"ChemicalElement_Substance",
								"CelestialBody_Object",*/
								"Planet"
							));
		return classNames;
	}
	

}
