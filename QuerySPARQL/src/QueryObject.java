import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;


public class QueryObject {
	static Map<String, CountObject> resultObject = new HashMap<String, CountObject>();
	static Map<String, InstanceObject> resultObjectInstance = new HashMap<String, InstanceObject>();
	/**
	   * Get class counts for passed kgClasses
	   * @param endpoint 0:dbpedia, 1:yago
	   * @return Map of resultObject<String, CountObject>
	   */
	public Map<String, CountObject> queryEndpoint(int endpoint) {
	//public Map<String, InstanceObject> queryEndpoint(int endpoint) {
		long startTime = System.nanoTime();
		String service = "";
		resultObject.clear();
		if (endpoint == 0) {
			service = "http://dbpedia.org/sparql";
		} else if (endpoint == 1) {
			service = "https://linkeddata1.calcul.u-psud.fr/sparql";
		}
		String queryTest = "ASK {}";
		QueryExecution qeTest = QueryExecutionFactory.sparqlService(service, queryTest);
		
		try {
			if(qeTest.execAsk()) {
				System.out.println(service + " is up");
				
					
				String[] kgClasses = null;
								
				if (endpoint == 0) {
					kgClasses =  getDBpediaClasses();
				} else if (endpoint == 1) {
					kgClasses = getYAGOclasses();
				}
				getClassCount(endpoint, service, kgClasses, false);
				getClassDegrees(endpoint, service, kgClasses);
				getClassInstaceDegrees(endpoint, service, kgClasses);	
				
				//getClassInstances(endpoint, service, kgClasses); // DBpedia endpoint LIMIT of 10,000 rows
				
			}
					
		} catch (QueryExceptionHTTP e) {
			System.out.println(service + " is down: " + e);
		} finally {
			qeTest.close();
			
			System.out.println("DONE for " + service + " in " + TimeUnit.SECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) +  " seconds" );
		}
		//return resultObjectInstance;
		return resultObject;
	}
	
	
	/**
	   * Get class instances for all classes
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param kgClasses array of all class names
	   */
	private void getClassInstances(int endpoint, String service,
			String[] kgClasses) {
		//create dict-like object for storing an InstanceObj for each class
		Map<String, InstanceObject> classInstanceDict = new HashMap<String, InstanceObject>();
		for (String className : kgClasses) {
			//create instanceObj
			InstanceObject instanceObj = new InstanceObject(className);
			// create query & send query to endpoint
			String queryString = getClassInstanceQueryString(endpoint, className);
			ResultSet results = queryEndpoint(service, queryString);
			//process results: add labels to instanceObj
			while (results.hasNext()) {
				QuerySolution sol = results.next();
				if (sol.get("label").isLiteral()) {
					//instanceObj.addInstance(sol.getLiteral("label").toString());
					instanceObj.addInstance(sol.getResource("i").toString(), sol.getLiteral("label").toString());
				}
			}
			
			//add instanceObj to classInstanceDict
			classInstanceDict.put(className, instanceObj);
			System.out.println(className + " has "+ instanceObj.getNumberOfInstancesMap() + " instances. " + instanceObj.getDuplicateCounterMap() + " have more than one english label.");
		}
		/*for (Map.Entry<String, InstanceObject> entry : classInstanceDict.entrySet()) {
			System.out.println(entry.getKey() + " has " + entry.getValue().getNumberOfInstances() + " instances");
		}*/
		resultObjectInstance = classInstanceDict;
		
	}


	private String getClassInstanceQueryString(int endpoint, String className) {
		String queryString = "";
		queryString = getQueryPrefix();
		queryString = queryString +
				"SELECT ?i ?label WHERE {\n";
		if (endpoint == 0) { //dbpedia
			queryString = queryString +
					"?i a <http://dbpedia.org/ontology/" + className + "> .\n"+
					" ?i rdfs:label ?label .\n FILTER langMatches( lang(?label), 'EN' )}";
		} else if (endpoint == 1) { //yago
			queryString = queryString +
					"?i a <http://yago-knowledge.org/resource/" + className + "> .\n"+
					" ?i rdfs:label ?label .\n FILTER langMatches( lang(?label), 'ENG' )}";
		}
		return queryString;
	}


	/**
	   * Get class counts for passed kgClasses
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param kgClasses array of all class names
	   * @param print boolean: print results in console
	   */
	private static void getClassCount(int endpoint, String service,
			String[] kgClasses, boolean print) {
		for (String className : kgClasses) {
			//create count object for each class an add it to the resultObject map
			CountObject countObj = new CountObject(className);
			resultObject.put(className, countObj);
			
			String queryString = "";
			queryString = getQueryPrefix();
			queryString = queryString +
					"SELECT (COUNT(?i) AS ?instanceCount) WHERE {\n";
			if (endpoint == 0) { //dbpedia
				queryString = queryString +
						"?i a <http://dbpedia.org/ontology/" + className + "> .}";
			} else if (endpoint == 1) { //yago
				queryString = queryString +
						"?i a <http://yago-knowledge.org/resource/" + className + "> .}";
			}
			ResultSet results = queryEndpoint(service, queryString);
			while (results.hasNext()) {
				QuerySolution sol = results.next();
				
                
				Integer iC = 0;
				if (sol.get("instanceCount").isLiteral()) {
					iC = sol.getLiteral("instanceCount").getInt();
					//if (!classCountsMap.containsKey(className)) {
					//	classCountsMap.put(className, iC);
						
						
					//} else {
					//	System.out.println("classCountsMap already contains a value for "+ className);
					//}
				} else {
					System.out.println("sol is not a literal");
				}
				countObj.setCount(iC);
				
			}
			if (print) {
				System.out.println(className + " count: " + countObj.getCount());
			}
			
		}
		
	}


	/**
	   * Get class instance degrees
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param kgClasses array of all class names
	   */
	private static void getClassInstaceDegrees(int endpoint, String service, String[] kgClasses) {
		
		for (String className : kgClasses) {
			//System.out.println(className);
			CountObject countObj = resultObject.get(className);
					
			getClassInstancesIndegree(endpoint, service, className, countObj.getCount());
			getClassInstancesOutdegree(endpoint, service, className, countObj.getCount());			
		}
	}

	/**
	   * Get class degrees
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param kgClasses array of all class names
	   */
	private static void getClassDegrees(int endpoint, String service, String[] kgClasses) {	
		for (String className : kgClasses) {
			CountObject countObj = resultObject.get(className);
			int indegree = 0;
			int outdegree = 0;
			
			indegree = getClassIndegree(endpoint, service, className);
			outdegree = getClassOutdegree(endpoint, service, className);	
			
			countObj.setIndegree(indegree);
			countObj.setOutdegree(outdegree);					
		}		
	}
	/**
	   * Get class instances outdegree (min, avg, median, max)
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param className string
	   * @param classCount class instance count (required for median calculation)
	   */
	private static void getClassInstancesOutdegree(int endpoint, String service,
			String className, Integer classCount) {
		
		String queryString = getQueryPrefix();
		queryString = queryString + 	
				"SELECT (MIN(?outdegree) AS ?minOutdegree) (AVG(?outdegree) AS ?avgOutdegree) (MAX(?outdegree) AS ?maxOutdegree) WHERE {\n"+
					"SELECT ?iI (COUNT(?pI) AS ?outdegree) WHERE {\n";
		//check endpoint
		if (endpoint == 0) {				
			queryString = queryString + 	
				"?iI a <http://dbpedia.org/ontology/" +className + "> .\n";
		} else if (endpoint == 1) {
			queryString = queryString + 	
					"?iI a <http://yago-knowledge.org/resource/" +className + "> .\n";
		}
		queryString = queryString + 	
						"OPTIONAL {?iI ?pI ?oI .}\n"+
					"}\n"+
					"GROUP BY ?iI\n"+
				"}";
		//queryEndpoint(service, queryString, true, false);
		String varNames[] = {"minOutdegree","avgOutdegree","maxOutdegree"};
		queryEndpoint(service, queryString, className, varNames);
		
		//median
		Double median = 0.0;
		//Double instancesHavingOutdegree = getNumberOfInstancesHavingDegree(endpoint, service, className, "medianOutdegree");
		//...
		//Integer offsetValue =  0;
		
		queryString = "";
		queryString = getQueryPrefix();
		//if (classCount == 0) {
		//	System.out.println("No instances. All instance outdegrees are 0");
		if (classCount != 0) {
			if ((classCount % 2) == 0) {
				//even
				queryString = queryString + 
						"SELECT (AVG(?preMedianOutdegree) AS ?medianOutdegree) WHERE {\n"+
					  "SELECT (?outdegree AS ?preMedianOutdegree) WHERE {\n"+
					    "SELECT ?outdegree WHERE {\n"+
					      "SELECT ?iI (COUNT(?pI) AS ?outdegree)  WHERE {\n";
				// check endpoint
				if (endpoint == 0) {				
					queryString = queryString + 	
							"?iI a <http://dbpedia.org/ontology/" +className + "> .\n";
				} else if (endpoint == 1) {
					queryString = queryString + 	
							"?iI a <http://yago-knowledge.org/resource/" +className + "> .\n";
				}
				queryString = queryString +
					        "OPTIONAL {?iI ?pI ?oI .}\n"+
					      "}\n"+
					      "GROUP BY ?iI\n"+
					    "}\n"+
					    "ORDER BY DESC (?outdegree)\n"+
					  "}\n"+
					  "LIMIT 2\n"+
					  "OFFSET "+ (classCount/2) +"\n"+
					"}";
				
			} else {
				//not even
				queryString = queryString + 
						"SELECT (?outdegree AS ?medianOutdegree) WHERE {\n"+
							"SELECT ?iI ?outdegree WHERE {\n"+
								"SELECT ?iI (COUNT(?pI) AS ?outdegree)  WHERE {\n";
				// check endpoint
				if (endpoint == 0) {				
					queryString = queryString + 	
							"?iI a <http://dbpedia.org/ontology/" +className + "> .\n";
				} else if (endpoint == 1) {
					queryString = queryString + 	
							"?iI a <http://yago-knowledge.org/resource/" +className + "> .\n";
				}
				queryString = queryString +
									"OPTIONAL {?iI ?pI ?oI .}\n"+
								"}\n"+
								"GROUP BY ?iI\n"+
							"}\n"+
							"ORDER BY DESC (?outdegree)\n"+
						"}\n"+
						"LIMIT 1\n"+
						"OFFSET "+ ((classCount/2)+1);
			}				
		//queryEndpoint(service, queryString, true, false);
		median = queryEndpoint(service, queryString, "medianOutdegree");
		
		}
		CountObject countObj = resultObject.get(className);
		countObj.setMedianOutdegree(median);

	}
	/*private static Double getNumberOfInstancesHavingDegree(int endpoint, String service, String className,
			String degree) {
		
		Double numInstances = 0.0;
		String queryString = getQueryPrefix();
		queryString = queryString + "SELECT (COUNT(DISTINCT ?iI) AS ?instances) WHERE {\n";
		
		if (endpoint == 0) {				
			queryString = queryString + 	
					"?iI a <http://dbpedia.org/ontology/" +className + "> .\n";
		} else if (endpoint == 1) {
			queryString = queryString + 	
					"?iI a <http://yago-knowledge.org/resource/" +className + "> .\n";
		}
		switch(degree) {
			case "medianOutdegree":
				queryString = queryString + 	
					"?iI ?pI ?oI  .}";
				break;
			case "medianIndegree":
				queryString = queryString + 	
					"?sI ?pI ?iI  .}";
				break;
			default:
				System.out.println("error while getting the number of instances having at degree of at least 1.")
		}
		numInstances = queryEndpoint(service, queryString, "instances");
		return numInstances;
	}
	*/


	/**
	   * Get class instances indegree (min, avg, median, max)
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param className string
	   * @param classCount class instance count (required for median calculation)
	   */
	private static void getClassInstancesIndegree(int endpoint, String service,
			String className, Integer classCount) {
		
		
		String queryString = getQueryPrefix();
		queryString = queryString + 	
				"SELECT (MIN(?indegree) AS ?minIndegree) (AVG(?indegree) AS ?avgIndegree) (MAX(?indegree) AS ?maxIndegree) WHERE {\n"+
					"SELECT ?iI (COUNT(?pI) AS ?indegree) WHERE {\n";
		// check endpoint
		if (endpoint == 0) {				
			queryString = queryString + 	
					"?iI a <http://dbpedia.org/ontology/" +className + "> .\n";
		} else if (endpoint == 1) {
			queryString = queryString + 	
					"?iI a <http://yago-knowledge.org/resource/" +className + "> .\n";
		}
		queryString = queryString +
						"OPTIONAL {?sI ?pI ?iI .}\n"+
					"}\n"+
					"GROUP BY ?iI\n"+
				"}";
		//queryEndpoint(service, queryString, true, false);
		String varNames[] = {"minIndegree","avgIndegree","maxIndegree"};
		queryEndpoint(service, queryString, className, varNames);
		
		
		//median
		queryString = "";
		queryString = getQueryPrefix();
		//if (classCount == 0) {
		//	System.out.println("No instances. All instance indegrees are 0");
		if (classCount != 0) {
			if ((classCount % 2) == 0) {
				//even
				queryString = queryString + 
						"SELECT (AVG(?preMedianIndegree) AS ?medianIndegree) WHERE {\n"+
					  "SELECT (?indegree AS ?preMedianIndegree) WHERE {\n"+
					    "SELECT ?iI ?indegree WHERE {\n"+
					      "SELECT ?iI (COUNT(?pI) AS ?indegree)  WHERE {\n";
				// check endpoint
				if (endpoint == 0) {				
					queryString = queryString + 	
							"?iI a <http://dbpedia.org/ontology/" +className + "> .\n";
				} else if (endpoint == 1) {
					queryString = queryString + 	
							"?iI a <http://yago-knowledge.org/resource/" +className + "> .\n";
				}
				queryString = queryString +
					        "OPTIONAL {?sI ?pI ?iI .}\n"+
					      "}\n"+
					      "GROUP BY ?iI\n"+
					    "}\n"+
					    "ORDER BY DESC (?indegree)\n"+
					  "}\n"+
					  "LIMIT 2\n"+
					  "OFFSET "+ (classCount/2) +"\n"+
					"}";
				
			} else {
				//not even
				queryString = queryString + 
						"SELECT (?indegree AS ?medianIndegree) WHERE {\n"+
							"SELECT ?iI ?indegree WHERE {\n"+
								"SELECT ?iI (COUNT(?pI) AS ?indegree)  WHERE {\n";
				// check endpoint
				if (endpoint == 0) {				
					queryString = queryString + 	
							"?iI a <http://dbpedia.org/ontology/" +className + "> .\n";
				} else if (endpoint == 1) {
					queryString = queryString + 	
							"?iI a <http://yago-knowledge.org/resource/" +className + "> .\n";
				}
				queryString = queryString +
									"OPTIONAL {?sI ?pI ?iI .}\n"+
								"}\n"+
								"GROUP BY ?iI\n"+
							"}\n"+
							"ORDER BY DESC (?indegree)\n"+
						"}\n"+
						"LIMIT 1\n"+
						"OFFSET "+ ((classCount/2)+1);
			}				
		//queryEndpoint(service, queryString, true, false);
		System.out.println(className);
		Double median = queryEndpoint(service, queryString, "medianIndegree");
		CountObject countObj = resultObject.get(className);
		countObj.setMedianIndegree(median);
		}		
		
	}
	/**
	   * Get class outdegree
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param className string
	   * @return outdegree (Integer)
	   */
	private static Integer getClassOutdegree(int endpoint, String service, String className) {
		Double classOutdegree = 0.0;
		String queryString = getQueryPrefix();
		queryString = queryString + 	
					"SELECT (COUNT(?pI) AS ?outdegree" + className + ") WHERE {\n";		
		
		// check endpoint
		if (endpoint == 0) {				
			queryString = queryString + 	
					"<http://dbpedia.org/ontology/" + className +">  ?pI ?oI. }";
		} else if (endpoint == 1) {
			queryString = queryString + 	
					"<http://yago-knowledge.org/resource/" +className + "> ?pI ?oI. }";
		}
		
		classOutdegree = queryEndpoint(service, queryString, "outdegree"+className);
		
		return classOutdegree.intValue();
	}
	/**
	   * Get class indegree
	   * @param endpoint 0:dbpedia, 1:yago
	   * @param service  url of endpoint
	   * @param className string
	   * @return indegree (Integer)
	   */
	private static Integer getClassIndegree(int endpoint, String service, String className) {
		Double classIndegree = 0.0;
		String queryString = getQueryPrefix();
		queryString = queryString + 	
					"SELECT (COUNT(?pI) AS ?indegree" + className + ") WHERE {\n";
		// check endpoint
		if (endpoint == 0) {				
			queryString = queryString + 	
					"?sI ?pI <http://dbpedia.org/ontology/" + className +"> .}";
		} else if (endpoint == 1) {
			queryString = queryString + 	
					"?sI ?pI <http://yago-knowledge.org/resource/" +className + "> . }";
		}
						
		//queryEndpoint(service, queryString, true, false);
		classIndegree = queryEndpoint(service, queryString, "indegree"+className);
		
		return classIndegree.intValue();
		
	}
	
	/**
	   * Query the endpoint
	   * @param service  url of endpoint
	   * @param queryString
	   * @return ResultSet
	   */
	private static ResultSet queryEndpoint(String service, String queryString) {
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		ResultSet results = qe.execSelect();
		ResultSet resultCopy = ResultSetFactory.copyResults(results);
		
		return resultCopy;
	}
	/**
	   * Query the endpoint
	   * @param service  url of endpoint
	   * @param queryString
	   * @param varName: name of the variable
	   * @return result (Double)
	   */
	private static Double queryEndpoint(String service, String queryString, String varName) {
		Double resultD = 0.0;
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		ResultSet results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution sol = results.next();
		
			if (sol.get(varName).isLiteral()) {
				Integer resultI = sol.getLiteral(varName).getInt();
				resultD = (double) resultI;			
			}
		}
		return resultD;
	}
	/**
	   * Query the endpoint
	   * @param service  url of endpoint
	   * @param queryString
	   * @param className
	   * @param varNames: array of all variables
	   */
	private static void queryEndpoint(String service, String queryString, String className, String[] varNames) {
		CountObject countObj = resultObject.get(className);

		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		ResultSet results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution sol = results.next();
		
			for (String varName : varNames) {
				Double resultD = 0.0;
				if (sol.get(varName) != null) {
					if (sol.get(varName).isLiteral()) {
						//Integer resultI = sol.getLiteral(varName).getInt();
						resultD = sol.getLiteral(varName).getDouble();
					}
					// else if ...
				} 
				countObj.setValue(varName, resultD);
			}
		}			
	}
	/**
	   * Query the endpoint
	   * @param service  url of endpoint
	   * @param queryString
	   * @param print: print result (boolean)
	   * @param printBlock: print result as block (boolean)
	   */
	/*private static void queryEndpoint(String service, String queryString, boolean print, boolean printBlock) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		ResultSet results = qe.execSelect();
		//print output
		if (print) {
			if (printBlock) {
				System.out.println(ResultSetFormatter.asText(results));
			} else {
				while (results.hasNext()) {
					String output = results.next().toString();
					System.out.println(output);
				}
			}
		}
	}
	*/
	/**
	   * Get query prefix 
	   * @return QueryPrefixString
	   */
	private static String getQueryPrefix() {
		String p = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX dbo: <http://dbpedia.org/ontology/>\n"+
					"PREFIX yago: <http://yago-knowledge.org/resource/>\n";
		return p;
	}
	/**
	   * Get all relevant classes from DBpedia
	   * @return Array of all DBpedia classes
	   */
	private static String[] getDBpediaClasses() {
		String classNameArray[] = {//"Taxon",
								//"Species",
							//PERSON
								"Agent",
								"Person",
								"Politician",
								"Athlete",
								"Actor",
								//"MovieDirector",
								//"Celebrity",
							//ORGANIZATION
								"GovernmentAgency",
								"Company",
								"PoliticalParty",
							//PLACE
								"Place",
								//"Location",
								//"Region",
								//"NaturalRegion",
								"PopulatedPlace",
								"City",
								"Village",
								"Town",
								"Country",
								//"Street",
								//"Beach",
								//"SkiArea",
								//"SkiResort",
								//"River",
								//"Airport",
								//"Building",
							//ART
								"Work",
								"MusicalWork",
								"Album",
								"Song",
								"Single",
								"Film",
								"Book",
								//"Image",
								//"Software",
							//EVENT	
								"Event",
								"MilitaryConflict",
								"SocietalEvent",
								"SportsEvent",
							//TRANSPORT
								"MeanOfTransportation",
								"Automobile",
								"Ship",
								"Spacecraft",
							//OTHER
								//"TimePeriod",
								//"CareerStation",
								//"Sport",
								"ChemicalSubstance",
								"ChemicalElement",
								"CelestialBody",
								"Planet"
								};
		return classNameArray;
	}
	/**
	   * Get all relevant classes from YAGO
	   * @return Array of all YAGO classes
	   */
	private String[] getYAGOclasses() {
		String classNameArray[] = {//"wordnet_object_100002684",
									//"wordnet_whole_100003553",
									//"wordnet_living_thing_100004258",
									//"wordnet_organism_100004475",
									//"wordnet_taxonomic_group_107992450",
									//"wordnet_species_108110373",
									//"wordnet_individual_110203839",
								//PERSON
									"yagoLegalActor",
									"wordnet_causal_agent_100007347",
									"wordnet_person_100007846",
									"wordnet_politician_110450303",
									"wordnet_politician_110451263",
									"wordnet_athlete_109820263",	
									"wordnet_actor_109767197", 
									"wordnet_actor_109765278",
									//"wordnet_director_110015215",	
									//"wordnet_celebrity_109903153",	
									//"wordnet_male_109624168",
									//"wordnet_group_100031264",
								//ORGANIZATION
									"wordnet_government_108050678",
									"wordnet_stock_company_108383310",
									"wordnet_party_108256968",
									"wordnet_company_108058098",
									"wordnet_business_108061042",
									"wordnet_enterprise_108056231",
								//PLACE
									"wordnet_location_100027167",
									//"wordnet_region_108630985",
									//"yagoGeoEntity",
									"wordnet_settlement_108672562",		
									"wordnet_city_108524735",
									"wordnet_village_108672738",	
									"wordnet_town_108665504",
									"wordnet_country_108544813",
									//"wordnet_street_104334599",		
									//"wordnet_beach_109217230",	
									//"wordnet_ski_resort_108652376",		
									//"wordnet_river_109411430",
									//"wordnet_airport_102692232",
									//"wordnet_building_102913152",
								//ART
									//"wordnet_abstraction_100002137",
									//"wordnet_artifact_100021939",
									"wordnet_work_104599396",
									//"wordnet_picture_106999436",
									//"wordnet_picture_103931044",									
									"wordnet_musical_composition_107037465",	
									"wordnet_album_106591815",
									"wordnet_song_107048000",
									"wordnet_movie_106613686",	
									"wordnet_book_106410904",
									"wordnet_book_102870092",
									//"wordnet_software_106566077",										
								//EVENT	
									"wordnet_event_100029378",
									"wordnet_war_101236296",
									"wordnet_social_event_107288639",
									"wordnet_conflict_100958896",
									"wordnet_tournament_107464725",
								//TRANSPORT	
									"wordnet_vehicle_104524313",
									"wordnet_conveyance_103100490",
									"wordnet_car_102958343",
									"wordnet_ship_104194289",
									"wordnet_spacecraft_104264914",
								//OTHER	
									"wordnet_chemical_element_114622893",	
									"wordnet_substance_100019613",
									"wordnet_celestial_body_109239740",
									"wordnet_planet_109394007"
									//"wordnet_time_interval_115269513",
									//"wordnet_noun_106319293",
									//"wordnet_sport_100523513",
									};
		return classNameArray;
	}

}