import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


public class EstimatedInstanceOverlap {

	public void run(ArrayList<String> classNames, ClassMapping cM, ArrayList<String> stringM, ArrayList<Double> thresholds) throws IOException {
		
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("estimatedInstanceOverlap.csv"));
			//for each x2y (d2y, d2o, y2d, o2d)
			//String x2y = "d2y";
			ArrayList<String> x2yA = new ArrayList<String>();
			x2yA.add("d2y");
			x2yA.add("d2o");
			x2yA.add("o2d");
			x2yA.add("y2d");
			
			
			for (String x2y : x2yA) {
				String k = x2y.substring(0, 1);			
				//for each className
				for (String className : classNames) {
					HashMap<String, ArrayList<String>> classMap = cM.getClassMap(className);
					if (classMap.containsKey(k)) {
						//for each KGclass
						for (String kgClass : classMap.get(k)) {
							// read partial gold standard (owl:sameAs links)							
							HashSet<Pair<String, String>> r_p = readGoldStandard(k, x2y, kgClass);
							//System.out.println("r_p.size(): " + r_p.size());
							//for each simMeasure
							for (String simMeasure : stringM) {
							//String simMeasure = "jaroWinkler";							
								//for each threshold
								for (Double threshold : thresholds) {
									if (! (simMeasure.equals("exactMatch") && threshold != 1.0)) {
										// read matching alignment
										HashSet<Pair<String, String>> a = readStringMatchingAlignment(k, x2y, kgClass, simMeasure, threshold);
										//System.out.println("a.size(): " + a.size());
										//get partial matching alignment
										HashSet<Pair<String, String>> a_p = getPartialMatchingAlignment(a, r_p);
										//System.out.println("a_p.size(): " + a_p.size());
										
										int tp = getTruePositives(r_p, a_p);										
										double recall = (double) tp / r_p.size();
										double precision = (double) tp / a_p.size();	
										double fMeasure = (2 * precision * recall) / (precision + recall);							
										double estimatedOverlap = (precision * a.size()) / recall;
										
										String results = x2y+", "+ kgClass + ", "+ simMeasure + ", " + threshold + ", "+ precision + ", " + recall + ", "+ fMeasure + ", " + estimatedOverlap + ", " + a.size() + ", " + a_p.size() + ", " + r_p.size() ;
										writer.write(results + "\n");
										
										System.out.println(results);
									}
								}
							}
						}
						
					}
				}
			}
			writer.close();
		}	catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	private int getTruePositives(HashSet<Pair<String, String>> r_p,
			HashSet<Pair<String, String>> a_p) {
		int tp = 0;
		for (Pair<String, String> rPair : r_p) {
			for (Pair<String, String> aPair : a_p) {
				if (rPair.equals(aPair)) {
					tp += 1;
				}
			}
		}
		return tp;
	}
	/**
	   * Defined as the subset of A which contains all elements in A which share at least one entity with an element in R′
	   * @param A
	   * @param R'
	   * @return HashSet<Pair<String, String> containing all pairs in the partial alignment
	   */
	private HashSet<Pair<String, String>> getPartialMatchingAlignment(
			HashSet<Pair<String, String>> a, HashSet<Pair<String, String>> r_p) {
		
		HashSet<Pair<String, String>> a_p = new HashSet<Pair<String, String>>();
		
		//get left and right entity of the partial gold standard R'
		HashSet<String> leftEntities = new HashSet<String>();
		HashSet<String> rightEntities = new HashSet<String>();
		for (Pair<String,String> r_p_pair : r_p) {
			leftEntities.add(r_p_pair.getLeft());
			rightEntities.add(r_p_pair.getRight());
		}
		
		//create A': add pair if A shares at least one entity with an element in the partial gold standard R'
		for (Pair<String, String> a_pair : a) {
			String aLeft = a_pair.getLeft();
			String aRight = a_pair.getRight();
			// check if at least one entity is shared
			if (leftEntities.contains(aLeft) || rightEntities.contains(aRight)) {
				a_p.add(a_pair);
			}
			
			
		}
		return a_p;
	}

	private HashSet<Pair<String, String>> readStringMatchingAlignment(String k,
			String x2y, String kgClass, String simMeasure, Double threshold) throws FileNotFoundException, IOException {	
		//String fileName = getFolderPath(k, x2y) + simMeasure + "/" + threshold + "/" + kgClass+".tsv";
		String fileName = "/Users/curtis/SeminarPaper_KG_files/simMeasureResults/"+x2y+"_"+kgClass+"_"+simMeasure+"_"+threshold+".tsv";		
		return readPairs(fileName);
	}

	private HashSet<Pair<String, String>> readGoldStandard(String k,
			String x2y, String kgClass) throws IOException {
		
		String fileName = getFolderPath(k, x2y) + "owlSameAs/"+kgClass+".tsv";
		return readPairs(fileName);
	}

	

	private HashSet<Pair<String, String>> readPairs(String fileName) throws FileNotFoundException, IOException {
		HashSet<Pair<String, String>> pairSet = new HashSet<Pair<String, String>>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] values = line.split("\t");
		    	if(values.length > 1) {
			    	//delete yago link for d2y case
					if (values[1].contains("http://yago-knowledge.org/resource/")) {
						//delete <http://yago-knowledge.org/resource/WORD_TO_KEEP>
						values[1] = "<" + values[1].substring(36, values[1].length());
					}
			    	Pair<String, String> p = new ImmutablePair<String, String>(values[0], values[1]);
			    	pairSet.add(p);
		    	}
		    }
		    br.close();
		} catch (FileNotFoundException fnfe){
			//System.out.println("no instances found for " + fileName);
		}
		return pairSet;
	}
	
	private String getFolderPath(String k, String x2y) {
		String folder = "";
		switch(k) {
		case "d":
			folder = "DBpedia"; 
			break;
		case "y":
			folder ="YAGO";
			break;
		case "o":
			folder = "OpenCyc";
			break;
		}
		return "/Users/curtis/SeminarPaper_KG_files/"+folder+"/"+x2y+"/";
	}

}
