import java.util.HashMap;
import java.util.Map;


public class QuerySPARQL {

	public static void main(String[] args) {
		Map<String, CountObject> results = new HashMap<String, CountObject>();
		int endpoint = 0;
		
		// query DBpedia
		QueryObject dbpedia = new QueryObject();
		results = dbpedia.queryEndpoint(endpoint);
		printHeader();
		printResults(results);
		
		
		
		// query YAGO
		endpoint = 1;
		QueryObject yago = new QueryObject();
		results = yago.queryEndpoint(endpoint);
		printHeader();
		printResults(results);
		 
	
	}
	private static void printResults(Map<String, CountObject> results) {
		for (Map.Entry<String, CountObject> entry : results.entrySet()) {
		    results.get(entry.getKey()).printAll();
		}
	}
	public static void printHeader() {
		System.out.println("className, count, classIndegree, classOutdegree, "
				+ "classInstanceIndegreeMin, classInstanceIndegreeAvg, classInstanceIndegreeMed, classInstanceIndegreeMax, "
				+ "classInstanceOutdegreeMin, classInstanceOutdegreeAvg, classInstanceOutdegreeMed, classInstanceOutdegreeMax");
	}


}
