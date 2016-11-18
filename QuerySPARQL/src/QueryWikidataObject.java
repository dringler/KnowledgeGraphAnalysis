import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;


public class QueryWikidataObject {
	
	public void queryEndpoint() {

		long startTime = System.nanoTime();
		String service = "https://query.wikidata.org/sparql";
		
		String queryTest = "ASK {}";
		QueryExecution qeTest = QueryExecutionFactory.sparqlService(service, queryTest);
		
		try {
			if(qeTest.execAsk()) {
				System.out.println(service + " is up");
				
				Map<String, Integer> propertiesIndegrees = new HashMap<String, Integer>();
				Map<String, Integer> propertiesOutdegrees = new HashMap<String, Integer>();
				
				//get list of all instances for a specific class
				List<String> instanceList = getInstancesOfClass(service, "Q515"); 
				//Q515: city, Q134556: single, , 
				//done
				//Q3010205: celebration, Q2066131: sportsperson, Q6256: country,  Q40218: spacecraft, Q198: war, Q334166: mode of transportation, Q634: planet, Q11344: chemical element, Q6999: astronomical object, 
				// get indegree and outdegree values for each class
				
				Map<String, Integer> instanceIndegrees = getInstaceDegrees(service, instanceList, 0);
				Map<String, Integer> instanceOutdegrees = getInstaceDegrees(service, instanceList, 1);
				
				//PRINT TOP x ENTRIES FOR EACH SET
				int topI = 10;
				System.out.println("TOP " + topI + " entries for instanceIndegrees");
				List<String> topInstancesIndegree = getTopXentries(instanceIndegrees, topI);
				System.out.println("TOP " + topI + " entries for instanceOutdegrees");
				List<String> topInstancesOutdegree = getTopXentries(instanceOutdegrees, topI);
				
				getInstanceProperties(service, instanceIndegrees, propertiesIndegrees, 0);
				getInstanceProperties(service, instanceOutdegrees, propertiesOutdegrees, 1);
				
				System.out.println("TOP " + topI + " properties for instanceIndegrees");
				List<String> topPropertiesIndegree = getTopXentries(propertiesIndegrees, topI);
				System.out.println("TOP " + topI + " properties for instanceOutdegrees");
				List<String> topPropertiesOutdegree = getTopXentries(propertiesOutdegrees, topI);
				
				//System.out.println(propertiesIndegrees.size());
				//System.out.println(propertiesOutdegrees.size());
				
				//System.out.println(instanceList.size());
				
			}
		} catch (QueryExceptionHTTP e) {
			System.out.println(service + " is down: " + e);
		} finally {
			qeTest.close();
			
			System.out.println("DONE for " + service + " in " + TimeUnit.SECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) +  " seconds" );
		}
	}

	private void getInstanceProperties(String service,
			Map<String, Integer> instanceDegrees, Map<String, Integer> propertiesDegrees, int degree) {
		Map<String, Integer> instanceProperties = new HashMap<String, Integer>();
		String varName = "instance";
		for(Map.Entry<String, Integer> instance : instanceDegrees.entrySet()) {
			String className = instance.getKey();
			String queryString = "";
			queryString = "PREFIX wd: <http://www.wikidata.org/entity/>\n"+
							"PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"+		
							
							"SELECT ?prop (COUNT(?prop) AS ?count) WHERE {\n";
			//indegree
			if (degree == 0) {
				queryString = queryString + "?s ?prop wd:"+className+" .}\n"; 
			} else { //outdegree
				queryString = queryString + "wd:"+className+" ?prop ?o .}\n";
			}
			queryString = queryString + "GROUP BY ?prop";
								
			ResultSet results = sendQuery(service, queryString);
			while (results.hasNext()) {
				QuerySolution sol = results.next();
				if (sol.get("prop").isResource() && sol.get("count").isLiteral()) {
					String prop = sol.get("prop").toString();
					int count = sol.getLiteral("count").getInt();
					//check if property is already contained
					if (propertiesDegrees.containsKey(prop)) {
						count += propertiesDegrees.get(prop); 
					}
					//update count
					propertiesDegrees.put(prop, count);			
				} else {
					System.out.println("prop is not a resource and count are not a literal");
				}
			}		
		}
	}
	static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

		Collections.sort(sortedEntries, 
		    new Comparator<Entry<K,V>>() {
		        @Override
		        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
		            return e2.getValue().compareTo(e1.getValue());
		        }
		    }
		);
		return sortedEntries;
	}

	private List<String> getTopXentries(Map<String, Integer> instanceDegrees,
			int max) {
		List<String> topInstances = new ArrayList<String>();
		//sort keys (degree values) in desc order
		List<Entry<String, Integer>> sortedList = entriesSortedByValues(instanceDegrees);
		//NavigableSet<Integer> sortedKeys = instanceIndegrees.descendingKeySet();
		
		//print top 10 classes 
		int i = 0;
		for (Entry e : sortedList) {
			System.out.println(e.getKey().toString() + ": " + e.getValue().toString());
			topInstances.add(e.getKey().toString());
			i++;
			if (i == max) {
				break;
			}
		}
		return topInstances;	
	}

	private Map<String, Integer> getInstaceDegrees(String service,
			List<String> instanceList, int degree) {
		Map<String, Integer> instanceIndegrees = new HashMap<String, Integer>();
		String varName = "instance";
		for (String instance : instanceList) {
			String queryString = "";
			queryString = "PREFIX wd: <http://www.wikidata.org/entity/>\n"+
							"PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"+		
							
							"SELECT (COUNT(?prop) AS ?degree) WHERE {\n";
			//indegree
			if (degree == 0) {
				queryString = queryString + "?s ?prop wd:"+instance+" .}"; 
			} else { //outdegree
				queryString = queryString + "wd:"+instance+" ?prop ?o .}";
			}
									
			ResultSet results = sendQuery(service, queryString);
			while (results.hasNext()) {
				QuerySolution sol = results.next();
				if (sol.get("degree").isLiteral()) {
					instanceIndegrees.put(instance, sol.getLiteral("degree").getInt()); 			
				}
			}	
		}
		return instanceIndegrees;
	}

	private List<String> getInstancesOfClass(String service, String classString) {
		List<String> instanceList = new ArrayList<String>();
		String varName = "instance";
		String queryString = "";
		queryString = "PREFIX wd: <http://www.wikidata.org/entity/>\n"+
						"PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"+		
						"SELECT ?"+varName+" WHERE {\n"+
						"?"+varName+" wdt:P31 wd:"+ classString + " .}\n"; 
		
		ResultSet results = sendQuery(service, queryString);
		//System.out.println(ResultSetFormatter.asText(results));
		
		while (results.hasNext()) {
			QuerySolution sol = results.next();
			if (sol.get(varName).isResource()) {
				//get full instance URI
				String instanceName = sol.get(varName).toString();
				// remove "http://www.wikidata.org/entity/" of URI
				instanceName = instanceName.substring(31);
				instanceList.add(instanceName);
			}
		}
		return instanceList;
	}

	private ResultSet sendQuery(String service, String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		ResultSet results = qe.execSelect();	
		return results;
	}

}
