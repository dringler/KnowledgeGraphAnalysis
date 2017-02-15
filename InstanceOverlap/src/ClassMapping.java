import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ClassMapping {
	HashMap<String, HashMap<String, ArrayList<String>>> fullClassMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
	String className;
	//Map<String, String> singleClassMap = new HashMap<String, String>();
	
	public ClassMapping() {
		this.fullClassMap = initFullClassMap();
	}
	
	public HashMap<String, ArrayList<String>> getClassMap(String className) {	
		return this.fullClassMap.get(className);
	}

	private HashMap<String, HashMap<String, ArrayList<String>>> initFullClassMap() {
		HashMap<String, HashMap<String, ArrayList<String>>> fullClassMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
		
		//PERSON
			//Legal or causal agent
			fullClassMap.put("Agent", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Agent")));
						put("y", new ArrayList<String>(Arrays.asList("yagoLegalActor", "wordnet_causal_agent_100007347")));
						put("w", new ArrayList<String>(Arrays.asList("Q24229398")));//agent
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVinb5wpEbGdrcN5Y29ycA", "Mx4rvVinsZwpEbGdrcN5Y29ycA")));//intelligent agent, legal agent
						put("n", new ArrayList<String>(Arrays.asList("humanagent", "agent")));
					}}
			);
			//Person or Human
			fullClassMap.put("Person", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Person")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_person_100007846")));
						put("w", new ArrayList<String>(Arrays.asList("Q5", "Q215627")));//human, person
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvViAkpwpEbGdrcN5Y29ycA"))); //person
						put("n", new ArrayList<String>(Arrays.asList("person")));
					}}
			);
			//Politician
			fullClassMap.put("Politician", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Politician")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_politician_110450303", "wordnet_politician_110451263")));
						put("w", new ArrayList<String>(Arrays.asList("Q82955")));//politician
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjntpwpEbGdrcN5Y29ycA"))); //politician
						put("n", new ArrayList<String>(Arrays.asList("politician")));
					}}
			);	
			//ATHLETE
			fullClassMap.put("Athlete", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Athlete")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_athlete_109820263")));
						put("w", new ArrayList<String>(Arrays.asList("Q2066131")));//sportsperson
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVi--5wpEbGdrcN5Y29ycA"))); //athlete
						put("n", new ArrayList<String>(Arrays.asList("athlete")));
					}}
			);
			//ACTOR
			fullClassMap.put("Actor",
					new HashMap<String, ArrayList<String>>() {{
						put("d", new ArrayList<String>(Arrays.asList("Actor")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_actor_109767197", "wordnet_actor_109765278")));
						put("w", new ArrayList<String>(Arrays.asList("Q33999")));//actor
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjaHZwpEbGdrcN5Y29ycA"))); //actor
						put("n", new ArrayList<String>(Arrays.asList("actor")));
					}}
			);
		//ORGANIZATION		
			//Government organization or agency
			fullClassMap.put("GovernmentOrganization", 
				new HashMap<String, ArrayList<String>>() {{
					put("d",  new ArrayList<String>(Arrays.asList("GovernmentAgency")));
					put("y", new ArrayList<String>(Arrays.asList("wordnet_government_108050678")));
					put("w", new ArrayList<String>(Arrays.asList("Q327333")));//government agency
					put("o", new ArrayList<String>(Arrays.asList("Mx4rv3HKmpwpEbGdrcN5Y29ycA")));//governmental organization
					put("n", new ArrayList<String>(Arrays.asList("governmentorganization")));
				}}
			);
			//Publicly held corporation or (stock) company
			fullClassMap.put("Company", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Company")));
						//put("y", new ArrayList<String>(Arrays.asList("wordnet_stock_company_108383310"))); //no instances
						put("w", new ArrayList<String>(Arrays.asList("Q891723")));//public company
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjZ_ZwpEbGdrcN5Y29ycA")));//publicly held corporation
						put("n", new ArrayList<String>(Arrays.asList("company")));
					}}
			);
			//PoliticalParty
			fullClassMap.put("PoliticalParty", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("PoliticalParty")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_party_108256968")));
						put("w", new ArrayList<String>(Arrays.asList("Q7278")));//political party
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVj82pwpEbGdrcN5Y29ycA")));//political party
						put("n", new ArrayList<String>(Arrays.asList("politicalparty")));
					}}
			);
		//PLACE
			//(geographic) place or location
			fullClassMap.put("Place", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Place")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_location_100027167")));
						put("w", new ArrayList<String>(Arrays.asList("Q2221906")));//geographic location
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjTtJwpEbGdrcN5Y29ycA")));//place
						put("n", new ArrayList<String>(Arrays.asList("location")));
					}}
			);
			//populated place or (human) settlement
			fullClassMap.put("PopulatedPlace", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("PopulatedPlace")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_settlement_108672562")));
						put("w", new ArrayList<String>(Arrays.asList("Q486972")));//human settlement
						put("o", new ArrayList<String>(Arrays.asList("Mx4rrPJDpCTfQdeS8IqP1a0lBw")));//populated place
						put("n", new ArrayList<String>(Arrays.asList("geopoliticallocation")));
					}}
			);
			//City / Village / Town
			fullClassMap.put("City_Village_Town", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("City", "Village", "Town")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_city_108524735", "wordnet_village_108672738", "wordnet_town_108665504")));
						put("w", new ArrayList<String>(Arrays.asList("Q515", "Q532", "Q3957")));//city, village, town
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjnZ5wpEbGdrcN5Y29ycA", "Mx4rv33BppwpEbGdrcN5Y29ycA")));//city, village
						put("n", new ArrayList<String>(Arrays.asList("city")));
					}}
			);
			//Country
			fullClassMap.put("Country", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Country")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_country_108544813")));
						put("w", new ArrayList<String>(Arrays.asList("Q6256")));//country
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvViIeZwpEbGdrcN5Y29ycA")));//country
						put("n", new ArrayList<String>(Arrays.asList("country")));
					}}
			);
		//ART
			//(creative or conceptual) work
			fullClassMap.put("Work", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Work")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_work_104599396")));
						put("w", new ArrayList<String>(Arrays.asList("Q386724")));//work
						put("o", new ArrayList<String>(Arrays.asList("Mx4rwClAZJwpEbGdrcN5Y29ycA")));//conceptual work
						put("n", new ArrayList<String>(Arrays.asList("creativework")));
					}}
			);
			//musical work
			fullClassMap.put("MusicalWork", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("MusicalWork")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_musical_composition_107037465")));
						put("w", new ArrayList<String>(Arrays.asList("Q2188189")));//musical work
						//put("o", new ArrayList<String>(Arrays.asList("Mx4rwAXXLZwpEbGdrcN5Y29ycA")));//audio conceptual work has no instances
						//put("n", new ArrayList<String>(Arrays.asList("")));
					}}
			);
			//album
			fullClassMap.put("Album", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Album")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_album_106591815")));
						put("w", new ArrayList<String>(Arrays.asList("Q482994")));//album
						put("o", new ArrayList<String>(Arrays.asList("Mx4rwLmi3JwpEbGdrcN5Y29ycA")));//album cw
						put("n", new ArrayList<String>(Arrays.asList("musicalbum")));
					}}
			);
			//song or music track
			fullClassMap.put("Song", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Song")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_song_107048000")));
						put("w", new ArrayList<String>(Arrays.asList("Q7302866")));//music track
						put("o", new ArrayList<String>(Arrays.asList("Mx4rwP3teJwpEbGdrcN5Y29ycA")));//song cw
						put("n", new ArrayList<String>(Arrays.asList("musicsong")));
					}}
			);
			//single
			fullClassMap.put("Single", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Single")));
						//put("y", new ArrayList<String>(Arrays.asList("")));
						put("w", new ArrayList<String>(Arrays.asList("Q134556")));//single
						put("o", new ArrayList<String>(Arrays.asList("Mx4rv6i4pJwpEbGdrcN5Y29ycA")));//single recording cw
						//put("n", new ArrayList<String>(Arrays.asList("")));
					}}
			);
			//movie or film
			fullClassMap.put("Movie", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Film")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_movie_106613686")));
						put("w", new ArrayList<String>(Arrays.asList("Q11424")));//film
						put("o", new ArrayList<String>(Arrays.asList("Mx4rv973YpwpEbGdrcN5Y29ycA")));//movie cw
						put("n", new ArrayList<String>(Arrays.asList("movie")));
					}}
			);
			//book
			fullClassMap.put("Book", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Book")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_book_106410904", "wordnet_book_102870092")));
						put("w", new ArrayList<String>(Arrays.asList("Q571")));//book
						//put("o", new ArrayList<String>(Arrays.asList("Mx4rwJaXepwpEbGdrcN5Y29ycA")));//book copy has no instances
						put("n", new ArrayList<String>(Arrays.asList("book")));
					}}
			);
		//EVENT
			//Event
			fullClassMap.put("Event", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Event")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_event_100029378")));
						put("w", new ArrayList<String>(Arrays.asList("Q1656682")));//event
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvViADZwpEbGdrcN5Y29ycA")));//event
						put("n", new ArrayList<String>(Arrays.asList("event")));
					}}
			);
			//Military or armed conflict or war
			fullClassMap.put("MilitaryConflict", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("MilitaryConflict")));
						//put("y", new ArrayList<String>(Arrays.asList("wordnet_war_101236296")));
						put("w", new ArrayList<String>(Arrays.asList("Q350604", "Q198")));//armed conflict, war
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvnSeAJwpEbGdrcN5Y29ycA")));//military conflict
						put("n", new ArrayList<String>(Arrays.asList("militaryeventtype", "militaryconflict")));
					}}
			);
			//SocietalEvent or celebration
			fullClassMap.put("SocietalEvent", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("SocietalEvent")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_social_event_107288639")));
						put("w", new ArrayList<String>(Arrays.asList("Q3010205")));//celebration
						//put("o", new ArrayList<String>(Arrays.asList("Mx4rvViPO5wpEbGdrcN5Y29ycA")));//social occurrence has no instances
						//put("n", new ArrayList<String>(Arrays.asList("")));
					}}
			);
			//SportsEvent
			fullClassMap.put("SportsEvent", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("SportsEvent")));
						//put("y", new ArrayList<String>(Arrays.asList("")));
						put("w", new ArrayList<String>(Arrays.asList("Q16510064")));//sporting event
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjzBJwpEbGdrcN5Y29ycA")));//sports event
						put("n", new ArrayList<String>(Arrays.asList("sportsevent")));
					}}
			);
		//TRANSPORT
			//(motor or road) Vehicle / Mean of Transportation
			fullClassMap.put("Vehicle", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("MeanOfTransportation")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_vehicle_104524313", "wordnet_conveyance_103100490")));
						put("w", new ArrayList<String>(Arrays.asList("Q42889", "Q334166")));//vehicle, mode of transport
						//put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjT95wpEbGdrcN5Y29ycA", "Mx4rvVjUgJwpEbGdrcN5Y29ycA", "Mx4rvVjVQJwpEbGdrcN5Y29ycA")));//road vehicle, conveyance, transportation device all have no instances
						put("n", new ArrayList<String>(Arrays.asList("vehicle")));
					}}
			);
			//car or automobile
			fullClassMap.put("Automobile", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Automobile")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_car_102958343")));
						put("w", new ArrayList<String>(Arrays.asList("Q1420")));//automobile
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvViVwZwpEbGdrcN5Y29ycA")));//automobile
						//put("n", new ArrayList<String>(Arrays.asList("")));
					}}
			);
			//ship or watercraft
			fullClassMap.put("Ship", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Ship")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_ship_104194289")));
						put("w", new ArrayList<String>(Arrays.asList("Q11446")));//ship
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVi-55wpEbGdrcN5Y29ycA")));//watercraft
						//put("n", new ArrayList<String>(Arrays.asList("")));
					}}
			);
			//spacecraft
			fullClassMap.put("Spacecraft", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Spacecraft")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_spacecraft_104264914")));
						put("w", new ArrayList<String>(Arrays.asList("Q40218")));//spacecraft
						put("o", new ArrayList<String>(Arrays.asList("Mx4rwQtlDpwpEbGdrcN5Y29ycA")));//spacecraft
						//put("n", new ArrayList<String>(Arrays.asList("")));
					}}
			);
		//OTHER
			//chemical element or substance
			fullClassMap.put("ChemicalElement_Substance", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("ChemicalSubstance"))); //ChemicalElement has no instances
						put("y", new ArrayList<String>(Arrays.asList("wordnet_chemical_element_114622893", "wordnet_substance_100019613")));
						put("w", new ArrayList<String>(Arrays.asList("Q79529", "Q11344")));//chemical substance, chemical element
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjNlZwpEbGdrcN5Y29ycA")));//chemical substance
						put("n", new ArrayList<String>(Arrays.asList("chemical")));
					}}
			);
			//astronomical or celestial body/object
			fullClassMap.put("CelestialBody_Object", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("CelestialBody")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_celestial_body_109239740")));
						put("w", new ArrayList<String>(Arrays.asList("Q6999")));//astronomical object
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjReJwpEbGdrcN5Y29ycA", "Mx4rvVjQs5wpEbGdrcN5Y29ycA")));//astronomical object, celestial body; "Mx4r-n4BEPzcQdaYSaNf0XKNIw" (celestial object) has no instances
						//put("n", new ArrayList<String>(Arrays.asList("")));
					}}
			);
			//planet
			fullClassMap.put("Planet", 
					new HashMap<String, ArrayList<String>>() {{
						put("d",  new ArrayList<String>(Arrays.asList("Planet")));
						put("y", new ArrayList<String>(Arrays.asList("wordnet_planet_109394007")));
						put("w", new ArrayList<String>(Arrays.asList("Q634")));//planet
						put("o", new ArrayList<String>(Arrays.asList("Mx4rvVjRL5wpEbGdrcN5Y29ycA")));//planet
						put("n", new ArrayList<String>(Arrays.asList("planet")));
					}}
			);
				
		return fullClassMap;
	}
	


	





}
