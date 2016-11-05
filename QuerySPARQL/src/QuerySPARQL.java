import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.arq.*;


public class queryDBpedia {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String service = "http://dbpedia.org/sparql";
		String queryTest = "ASK {}";
		QueryExecution qeTest = QueryExecutionFactory.sparqlService(service, queryTest);
			
		/*QueryEngineHTTP objectToExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(service, queryString);
		objectToExec.addParam("timeout","500000000"); //in milliseconds
		*/
 
			
		try {
			if(qeTest.execAsk()) {
				System.out.println(service + " is up");
				
				String dboClasses[] =  getDboClasses();
				Map<String, Integer> dboClassCounts = new HashMap<String, Integer>();
				dboClassCounts = getClassCount(service, dboClasses);
				getClassDegrees(service, dboClasses);
				getClassInstaceDegrees(service, dboClasses, dboClassCounts);	
			}
					
		} catch (QueryExceptionHTTP e) {
			System.out.println(service + " is down: " + e);
		} finally {
//			qe.close();
			qeTest.close();
			System.out.println("DONE");
			//objectToExec.close();
		}
	}



	private static Map<String, Integer> getClassCount(String service,
			String[] dboClasses) {
		Map<String, Integer> dboClassCountsMap = new HashMap<String, Integer>();
		for (String dboClass : dboClasses) {
			//System.out.println("Get class count for "+ dboClass);
			String queryString = getQueryPrefix();
			queryString = queryString +
					"SELECT (COUNT(?i) AS ?instanceCount) WHERE {\n"+
						"?i a <http://dbpedia.org/ontology/" + dboClass + "> .\n" +
					"}";
			ResultSet results = queryEndpoint(service, queryString);
			while (results.hasNext()) {
                QuerySolution sol = results.next();
				
				if (sol.get("instanceCount").isLiteral()) {
					Integer iC = sol.getLiteral("instanceCount").getInt();
					if (!dboClassCountsMap.containsKey(dboClass)) {
						dboClassCountsMap.put(dboClass, iC);
					} else {
						System.out.println("dboClassCountsMap already contains a value for "+ dboClass);
					}
				} else {
					System.out.println("sol is not a literal");
					dboClassCountsMap.put(dboClass, 0);
				}
				
			}
			
		}
		
		return dboClassCountsMap;
	}



	private static void getClassInstaceDegrees(String service, String[] dboClasses, Map<String, Integer> dboClassCounts) {
		for (String dboClass : dboClasses) {
			System.out.println(dboClass);
			getClassInstancesIndegree(service, dboClass, dboClassCounts.get(dboClass));
			getClassInstancesOutdegree(service, dboClass, dboClassCounts.get(dboClass));			
		}
		
	}


	private static void getClassDegrees(String service, String[] dboClasses) {
		for (String dboClass : dboClasses) {
			getClassIndegree(service, dboClass);
			getClassOutdegree(service, dboClass);			
		}		
	}

	private static void getClassInstancesOutdegree(String service,
			String dboClass, Integer classCount) {
		String queryString = getQueryPrefix();
		queryString = queryString + 	
				"SELECT (MIN(?outdegree) AS ?minOutdegree) (AVG(?outdegree) AS ?avgOutdegree) (MAX(?outdegree) AS ?maxOutdegree) WHERE {\n"+
					"SELECT ?iI (COUNT(?pI) AS ?outdegree) WHERE {\n"+ 
						"?iI a <http://dbpedia.org/ontology/" +dboClass + "> .\n" +
						"?iI ?pI ?oI .\n"+
					"}\n"+
					"GROUP BY ?iI\n"+
				"}";
		queryEndpoint(service, queryString, true, false);
		
		//median
		queryString = "";
		queryString = getQueryPrefix();
		if (classCount == 0) {
			System.out.println("No instances. All instance outdegrees are 0");
		} else {
			if ((classCount % 2) == 0) {
				//even
				queryString = queryString + 
						"SELECT (AVG(?preMedianOutdegree) AS ?medianOutdegree) WHERE {\n"+
					  "SELECT (?outdegree AS ?preMedianOutdegree) WHERE {\n"+
					    "SELECT ?outdegree WHERE {\n"+
					      "SELECT ?iI (COUNT(?pI) AS ?outdegree)  WHERE {\n"+
					        "?iI a <http://dbpedia.org/ontology/"+dboClass+"> .\n"+
					        "?iI ?pI ?oI .\n"+
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
								"SELECT ?iI (COUNT(?pI) AS ?outdegree)  WHERE {\n"+
									"?iI a <http://dbpedia.org/ontology/"+dboClass+"> .\n"+
									"?iI ?pI ?oI .\n"+
								"}\n"+
								"GROUP BY ?iI\n"+
							"}\n"+
							"ORDER BY DESC (?outdegree)\n"+
						"}\n"+
						"LIMIT 1\n"+
						"OFFSET "+ (classCount/2);
			}				
		queryEndpoint(service, queryString, true, false);
		}
	}

	private static void getClassInstancesIndegree(String service,
			String dboClass, Integer classCount) {
		String queryString = getQueryPrefix();
		queryString = queryString + 	
				"SELECT (MIN(?indegree) AS ?minIndegree) (AVG(?indegree) AS ?avgIndegree) (MAX(?indegree) AS ?maxIndegree) WHERE {\n"+
					"SELECT ?iI (COUNT(?pI) AS ?indegree) WHERE {\n"+ 
						"?iI a <http://dbpedia.org/ontology/" +dboClass + "> .\n" +
						"?sI ?pI ?iI .\n"+
					"}\n"+
					"GROUP BY ?iI\n"+
				"}";
		queryEndpoint(service, queryString, true, false);
		
		
		//median
		queryString = "";
		queryString = getQueryPrefix();
		if (classCount == 0) {
			System.out.println("No instances. All instance indegrees are 0");
		} else {
			if ((classCount % 2) == 0) {
				//even
				queryString = queryString + 
						"SELECT (AVG(?preMedianIndegree) AS ?medianIndegree) WHERE {\n"+
					  "SELECT (?indegree AS ?preMedianIndegree) WHERE {\n"+
					    "SELECT ?iI ?indegree WHERE {\n"+
					      "SELECT ?iI (COUNT(?pI) AS ?indegree)  WHERE {\n"+
					        "?iI a <http://dbpedia.org/ontology/"+dboClass+"> .\n"+
					        "?sI ?pI ?iI .\n"+
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
								"SELECT ?iI (COUNT(?pI) AS ?indegree)  WHERE {\n"+
									"?iI a <http://dbpedia.org/ontology/"+dboClass+"> .\n"+
									"?sI ?pI ?iI .\n"+
								"}\n"+
								"GROUP BY ?iI\n"+
							"}\n"+
							"ORDER BY DESC (?indegree)\n"+
						"}\n"+
						"LIMIT 1\n"+
						"OFFSET "+ (classCount/2);
			}				
		queryEndpoint(service, queryString, true, false);
		}
		
		
	}

	private static void getClassOutdegree(String service, String dboClass) {
		String queryString = getQueryPrefix();
		queryString = queryString + 	
					"SELECT (COUNT(?pI) AS ?outdegree" + dboClass + ") WHERE {\n"+
						"<http://dbpedia.org/ontology/" + dboClass +">  ?pI ?oI.\n"+
					"}";		
		queryEndpoint(service, queryString, true, false);
		
	}

	private static void getClassIndegree(String service, String dboClass) {
		String queryString = getQueryPrefix();
		queryString = queryString + 	
					"SELECT (COUNT(?pI) AS ?indegree" + dboClass + ") WHERE {\n"+
						"?sI ?pI <http://dbpedia.org/ontology/" + dboClass +"> .\n"+
					"}";			
		queryEndpoint(service, queryString, true, false);
		
	}

	private static ResultSet queryEndpoint(String service, String queryString) {
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		ResultSet results = qe.execSelect();
		ResultSet resultCopy = ResultSetFactory.copyResults(results);
		
		return resultCopy;
	}
	private static void queryEndpoint(String service, String queryString, boolean print, boolean printBlock) {
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

	private static String getQueryPrefix() {
		// TODO Auto-generated method stub
		String p = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
					"PREFIX dbo: <http://dbpedia.org/ontology/>\n";
		return p;
	}

	private static String[] getDboClasses() {
		// TODO Auto-generated method stub
		String dboClassArray[] = {"Taxon",
								"Species",
								"Agent",
								"Person",
								"Athlete",
								"Actor",
								"MovieDirector",
								"Celebrity",
								"GovernmentAgency",
								"Company",
								"Place",
								"Location",
								"Region",
								"NaturalRegion",
								"PopulatedPlace",
								"City",
								"Village",
								"Town",
								"Street",
								"Beach",
								"SkiArea",
								"SkiResort",
								"River",
								"Airport",
								"Work",
								"Image",
								"TimePeriod",
								"CareerStation",
								"Politician",
								"PoliticalParty",
								"MusicalWork",
								"Album",
								"Song",
								"Single",
								"Film",
								"Book",
								"Software",
								"Sport",
								"Event",
								"MilitaryConflict",
								"SocietalEvent",
								"SportsEvent",
								"Country",
								"Building",
								"ChemicalSubstance",
								"ChemicalElement",
								"CelestialBody",
								"Planet",
								"MeanOfTransportation",
								"Automobile",
								"Ship",
								"Spacecraft"
								};
		return dboClassArray;
	}

}
