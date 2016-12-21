import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InstanceOverlapMain {

	public static void main(String[] args) throws IOException {
		
		boolean useSamples = true;
		
		ArrayList<Double> thresholds = new ArrayList<Double>();
		thresholds.add(1.0);
		thresholds.add(0.9);
		thresholds.add(0.8);
		
		StringMeasures stringMeasures = new StringMeasures(thresholds);
		//StringMeasures stringMeasures = new StringMeasures(exactMatch, jaccard, jaccardT, jaro, jaroT, scaledLevenstein, scaledLevensteinT, tfidf, tfidfT, jaroWinkler, jaroWinklerT,softTfidf, softTfidfT, internalSoftTfidf, internalSoftTfidfS, internalSoftTfidfT);
		
		ArrayList<String> stringM = new ArrayList<String>();
		stringM.add("exactMatch");
		stringM.add("jaccard");
		stringM.add("jaro");
		stringM.add("jaroWinkler");
		stringM.add("scaledLevenstein");
		stringM.add("softTfidf");
		
		//configure log4j for secondstring library
		org.apache.log4j.BasicConfigurator.configure();
		//LogManager.getRootLogger().setLevel(Level.OFF); //set console logger off
		
		// PARAMETERS: string similarity measures and thresholds
		/*boolean exactMatch = true;
		boolean jaccard = true;
		double jaccardT = 1.0;
		boolean jaro = true;
		double jaroT = 1.0;
		boolean scaledLevenstein = true;
		double scaledLevensteinT = 1.0;
		boolean tfidf = false;
		double tfidfT = 1.0;
		boolean jaroWinkler = true;
		double jaroWinklerT = 1.0;
		boolean softTfidf = true;
		double softTfidfT = 1.0;
		boolean internalSoftTfidf = false;
		String internalSoftTfidfS = "jaroWinkler"; //"jaroWinkler", "jaccard", or "scaledLevenstein"
		double internalSoftTfidfT = 0.9;
		*/
		
		ClassMapping cM = new ClassMapping();
		ArrayList<String> classNames = getClassNames();
		for (String className : classNames) {
			
		
		// SAME AS LINKS
			// PARAMETERS		
			/*boolean d2y = true;
			boolean d2o = true;
			boolean y2d = true;
			boolean o2d = true;
			CountSameAs same = new CountSameAs();
			same.run(classNames, cM, d2y, d2o, y2d, o2d);
			*/
			
		// INSTANCE MATCHES USING STRING SIMILARITY MEASURES
			CountStringSimilarity stringSim = new CountStringSimilarity();
			stringSim.run(className, cM, stringMeasures, useSamples, thresholds);
		//CALCULATE ESTIMATED INSTANCE OVERLAP
			
			System.out.println("Start calculating estimated instance overlap for " + className);
			EstimatedInstanceOverlap overlap = new EstimatedInstanceOverlap();
			overlap.run(className, cM, stringM, thresholds);
			System.out.println("DONE");
			
		}
		
	
		
		
	
		

	}
	
	private static ArrayList<String> getClassNames() {
		ArrayList<String> classNames = new ArrayList<String>();
		classNames.addAll(Arrays.asList(
							//PERSON
							/*	"Agent",
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
								*/"Vehicle",/*
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
