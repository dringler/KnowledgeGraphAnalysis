import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


public class EstimatedInstanceOverlap {
	/**
	 * Calculate Estimated Instance Overlap and save results to disk (estimatedOverlap folder)
	 * @param className
	 * @param cM
	 * @param stringM
	 * @param thresholdsH
	 * @param thresholdsL
	 * @param kKgInstanceCount
	 * @throws IOException
	 */
	public void run(String className, 
			ClassMapping cM, 
			ArrayList<String> stringM, 
			ArrayList<Double> thresholdsH,
			ArrayList<Double> thresholdsL,
			ArrayList<Double> thresholdsJaccard,
			HashSet<String> simMeasuresThresholdH,
			HashMap<String, HashMap<String, Integer>> kKgInstanceCount,
			int maxBlockSize) throws IOException {
		System.out.println("Start calculating estimated instance overlap for " + className);
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		
		try {
			String fileName = "estimatedInstanceOverlap_"+className+"_wBlockingMax"+ maxBlockSize +"_tokenBk4_"+df.format(date)+".csv";
			BufferedWriter writer = new BufferedWriter(new FileWriter("./estimatedOverlap/"+ fileName));
			
			String header = "x2y, fromKgClass, fromInstanceCount, toKgClass, toInstanceCount, simMeasure, threshold, precision, recall, fMeasure, estimatedOverlap, owlSameAs links, matching alignment, partial matching alignment, tp";			
			writer.write(header + "\n");
			//for each x2y (d2y, d2o, y2d, o2d)
			//String x2y = "d2y";
			ArrayList<String> x2yA = new ArrayList<String>();
			x2yA.add("d2y");
			x2yA.add("d2w");
			x2yA.add("d2o");
			x2yA.add("d2n");
			x2yA.add("y2w");
			x2yA.add("y2o");
			x2yA.add("y2n");
			x2yA.add("w2o");
			x2yA.add("w2n");
			x2yA.add("o2n");
			
			ArrayList<Double> thresholds;
			
			for (String x2y : x2yA) {
			//x2yA.parallelStream().forEach((x2y) -> {
				String x = x2y.substring(0, 1);		
				String y = x2y.substring(2,3);
				
				HashMap<String, ArrayList<String>> classMap = cM.getClassMap(className);
				if (classMap.containsKey(x)) {
					//for each KGclass
					for (String kgClass : classMap.get(x)) {	
						//for each class in other kg
						if (classMap.get(y) != null) {
							for (String toKgClass : classMap.get(y)) {
								// read partial gold standard (owl:sameAs links)	
								System.out.println("Reading gold standard for "+  x2y + kgClass + "_" + toKgClass + "...");
								HashSet<Pair<String, String>> r_p = readGoldStandard(x, x2y, kgClass, toKgClass);
								System.out.println("Gold standard for received with size: " + r_p.size());
								//for each simMeasure
								for (String simMeasure : stringM) {
									thresholds = thresholdsL;
									if (simMeasuresThresholdH.contains(simMeasure)) {
										thresholds = thresholdsH;
									} else if (simMeasure.equals("jaccard")) {
										thresholds = thresholdsJaccard;
									}
									//for each threshold
									for (Double threshold : thresholds) {
										if (! ((simMeasure.equals("exactMatch") || simMeasure.equals("all")) && threshold != 1.0)) {
											// read matching alignment
											System.out.println("Calculate estimated overlap for " + simMeasure + " with threshold: " + threshold + " - Loading string matching alignment...");
											HashSet<Pair<String, String>> a = readStringMatchingAlignment(x, x2y, kgClass, toKgClass, simMeasure, threshold);
											//System.out.println("a.size(): " + a.size());
											//get partial matching alignment: parallelized withinin method
											System.out.println("String matching alignment loaded. Get partial matching alginment...");
											Set<Pair<String, String>> a_p = getPartialMatchingAlignment(a, r_p);
											//System.out.println("a_p.size(): " + a_p.size());
											
											int tp = getTruePositives(r_p, a_p); //parallized										
											double recall = (double) tp / r_p.size();
											double precision = (double) tp / a_p.size();	
											double fMeasure = (2 * precision * recall) / (precision + recall);							
											double estimatedOverlap = (precision * a.size()) / recall;
											
											String fromKgInstanceCount = getInstanceCount(kKgInstanceCount, x, kgClass); 
											String toKgInstanceCount = getInstanceCount(kKgInstanceCount, y, toKgClass);
											
											if (kKgInstanceCount.containsKey(y)) {
												if (kKgInstanceCount.get(y).containsKey(toKgClass)) {
													toKgInstanceCount = kKgInstanceCount.get(y).get(toKgClass).toString();
													String results = x2y+", "+
																	kgClass + ", " + fromKgInstanceCount + ", " +
																	toKgClass + ", "+ toKgInstanceCount+ ", " +
																	simMeasure + ", " + threshold + ", "+
																	precision + ", " + recall + ", "+ fMeasure + ", " +
																	estimatedOverlap  + ", " + r_p.size()  + ", " +
																	a.size() + ", " + a_p.size() + ", " +  tp;
													writer.write(results + "\n");
												}
											}
											//System.out.println(results);
										}
									}//end for threshold : thresholds	
								}//end for simMeasure : stringM
							}//end for toKgClass : classMap.get(y)
						}
					}
					
				}
			//});		
			}
			writer.close();
			System.out.println("results written to " + fileName);
		}	catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	/**
	 * Get instance count for a kgClass as String
	 * @param kKgInstanceCount
	 * @param k
	 * @param kgClass
	 * @return
	 */
	private String getInstanceCount(
			HashMap<String, HashMap<String, Integer>> kKgInstanceCount,
			String k, 
			String kgClass) {
		if (kKgInstanceCount.get(k) != null) {
			if (kKgInstanceCount.get(k).get(kgClass) != null) {
				return kKgInstanceCount.get(k).get(kgClass).toString(); 	
			}
		}
		return "0";
	}
	/**
	 * Get TP (true positive) matches
	 * @param r_p
	 * @param a_p
	 * @return number of TPs
	 */
	private int getTruePositives(Set<Pair<String, String>> r_p,
			Set<Pair<String, String>> a_p) {
		/*int tp = 0;
		for (Pair<String, String> rPair : r_p) {
			for (Pair<String, String> aPair : a_p) {
				if (rPair.equals(aPair)) {
					tp += 1;
				}
			}
		}*/		
		//parallelize
		Set<Pair<String, String>> tpPairs = r_p.stream()
				.parallel()
				.filter(rPair -> a_p.stream()
						.anyMatch(aPair -> aPair.equals(rPair)))
				.collect(Collectors.toSet());
		//System.out.println("tp = " + tp + ", tpPairs.size() = " + tpPairs.size());
		return tpPairs.size(); //tp
	}
	/**
	   * Defined as the subset of A which contains all elements in A which share at least one entity with an element in R′
	   * @param a A
	   * @param r_p R'
	   * @return HashSet<Pair<String, String> containing all pairs in the partial alignment
	   */
	private Set<Pair<String, String>> getPartialMatchingAlignment(
			HashSet<Pair<String, String>> a, 
			HashSet<Pair<String, String>> r_p) {
			
		//get left and right entity of the partial gold standard R'
		Set<String> leftEntities = new HashSet<String>();
		Set<String> rightEntities = new HashSet<String>();
		/*for (Pair<String,String> r_p_pair : r_p) {
			leftEntities.add(r_p_pair.getLeft());
			rightEntities.add(r_p_pair.getRight());
		}*/
		//System.out.println("for loop results: leftEntities.size() = " + leftEntities.size() + " rightEntities.size() = " + rightEntities.size());
		//parallelize
		leftEntities = r_p.stream()
						.parallel()
						.map(pair -> pair.getLeft())
						.collect(Collectors.toSet());
				
		rightEntities = r_p.stream()
						.parallel()
						.map(pair -> pair.getRight())
						.collect(Collectors.toSet());
			
		
		//create A': add pair if A shares at least one entity with an element in the partial gold standard R'
		/*for (Pair<String, String> a_pair : a) {
			String aLeft = a_pair.getLeft();
			String aRight = a_pair.getRight();
			// check if at least one entity is shared
			if (leftEntities.contains(aLeft) || rightEntities.contains(aRight)) {
				a_p.add(a_pair);
			}	
		}*/
		//parallelize
		Set<Pair<String, String>> a_p = getAp(a, leftEntities, rightEntities);	
		//System.out.println("a_p.size(): " + a_p.size() + " a_pP.size(): " + a_pP.size());
		return a_p;
		
	}
	/**
	   * Defined as the subset of A which contains all elements in A which share at least one entity with an element in R′
	   * @param a A
	   * @param leftEntities
	   * @param rightEntities
	   * @return a_p A':HashSet<Pair<String, String> containing all pairs in the partial alignment
	   */
	private Set<Pair<String, String>> getAp(HashSet<Pair<String, String>> a,
			Set<String> leftEntities, Set<String> rightEntities) {
		 Set<Pair<String, String>> a_p = a.stream()
				.parallel()
				.filter(a_pair -> (leftEntities.contains(a_pair.getLeft()) || rightEntities.contains(a_pair.getRight())))
				.collect(Collectors.toSet());
		return a_p;
	}
	/*private synchronized void addPairToA_p(Pair<String, String> a_pair,
			HashSet<Pair<String, String>> a_p) {
		a_p.add(a_pair);		
	}
	private synchronized void addEntries(Pair<String, String> r_p_pair,
			HashSet<String> leftEntities, HashSet<String> rightEntities) {
		leftEntities.add(r_p_pair.getLeft());
		rightEntities.add(r_p_pair.getRight());
	}*/
	/**
	 * Read String Matching Alignment from Disk (in simMeasureResults folder)
	 * @param k
	 * @param x2y
	 * @param kgClass
	 * @param toKgClass
	 * @param simMeasure
	 * @param threshold
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private HashSet<Pair<String, String>> readStringMatchingAlignment(
			String k,
			String x2y, 
			String kgClass, 
			String toKgClass, 
			String simMeasure, 
			Double threshold) throws FileNotFoundException, IOException {			
		String fileName = "./simMeasureResults/"+x2y+"_"+kgClass+"_"+toKgClass+"_"+simMeasure+"_"+threshold+".tsv";
		return readPairs(fileName);
	}
	/**
	 * Read gold standard from file (in owlSameAs folder)
	 * @param k
	 * @param x2y
	 * @param kgClass
	 * @param toKgClass
	 * @return
	 * @throws IOException
	 */
	private HashSet<Pair<String, String>> readGoldStandard(String k,
			String x2y, String kgClass, String toKgClass) throws IOException {
		String fileName = "./owlSameAs/"+x2y+"/"+kgClass + "_" + toKgClass +".tsv";
		return readPairs(fileName);
	}

	
	/**
	 * Read file from disk
	 * @param fileName
	 * @return pairSet (HashSet<Pair<String, String>>)
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private HashSet<Pair<String, String>> readPairs(String fileName) throws FileNotFoundException, IOException {
		HashSet<Pair<String, String>> pairSet = new HashSet<Pair<String, String>>();	
		//parallelize
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
					//delete yago link for y2w,o,n case
					if (values[0].contains("http://yago-knowledge.org/resource/")) {
						//delete <http://yago-knowledge.org/resource/WORD_TO_KEEP>
						values[0] = "<" + values[0].substring(36, values[0].length());
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
	
}
