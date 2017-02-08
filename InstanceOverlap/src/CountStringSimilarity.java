import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


public class CountStringSimilarity {
	private boolean useSamples; 
	//similarity measure strings
	private String jaccardS = "jaccard";
	private String jaroS = "jaro";
	private String scaledLevensteinS = "scaledLevenstein";
	private String tfidfS = "tfidf";
	private String jaroWinklerS ="jaroWinkler";
	private String exactMatchS = "exactMatch";
	private String softTfidfS = "softTfidf";
	private String allS = "all";


	public HashMap<String, HashMap<String, Integer>> run(String className, ClassMapping cM, StringMeasures stringMeasures, boolean useSamples, ArrayList<Double> thresholds) {
		System.out.println("Start CountStringSimilarity.run()");
		this.useSamples = useSamples;
		//long startTime = System.nanoTime();
			
		CountStringSimilarityResults results = new CountStringSimilarityResults();
		/*StringSimilarityPairs resultPairsD2y = new StringSimilarityPairs();
		StringSimilarityPairs resultPairsD2o = new StringSimilarityPairs();
		StringSimilarityPairs resultPairsY2d = new StringSimilarityPairs();
		StringSimilarityPairs resultPairsO2d = new StringSimilarityPairs();*/
	
		//instanceLabels: HashMap<k, <HashMap<kgClass,<HashMap<instanceURI, <HashSet<englishLabels>>>>
		HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> kKgClassInstanceLabels = new HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>();
		HashMap<String, HashMap<String, Integer>> kKgClassInstanceCounts = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, ArrayList<String>> classMap = cM.getClassMap(className);//key: d,w,y,o,n ; value:kgC
		System.out.println(classMap);
		
		//get instances for each kgClass with all labels
		kKgClassInstanceLabels = getInstanceLabels(classMap);
		/*System.out.println(instanceLabels.get("d"));
		System.out.println(instanceLabels.get("y"));
		System.out.println(instanceLabels.get("n"));
		System.out.println(instanceLabels.get("o"));
		System.out.println(instanceLabels.get("w"));
		*/
		for (String k : kKgClassInstanceLabels.keySet()) {
			for (String kgClass : kKgClassInstanceLabels.get(k).keySet()) {
				int kgClassSize = kKgClassInstanceLabels.get(k).get(kgClass).size();			
				if (kKgClassInstanceCounts.containsKey(k)) {
					kKgClassInstanceCounts.get(k).put(kgClass, kgClassSize);
				} else {
					HashMap<String, Integer> kgClassMap = new HashMap<>();
					kgClassMap.put(kgClass, kgClassSize);
					kKgClassInstanceCounts.put(k, kgClassMap);
				}
			}
		}
		
		//STANDARD TOKEN BLOCKING
		//create blocks
		/*
		 *HashMap<token, <HashMap<k, HashMap<kgClass, <HashSet<URIs>>>>>> 
		 */
		HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> blocks = createBlocks(kKgClassInstanceLabels);
		
			
		//getMatchedStringCounts(results, kKgClassInstanceLabels, stringMeasures);
		getMatchedStringPairs(results, kKgClassInstanceLabels, stringMeasures, thresholds, blocks);
			
		//print results
		/*Set<Pair<String, String>> allPairs = results.getPairs();
		for (Pair<String,String> p : allPairs) {
			System.out.println(p.getLeft() + "_" + p.getRight() +  ":" + results.getInstanceOverlapCount(p.getLeft(), p.getRight()));
		}
		*/
			
		return kKgClassInstanceCounts;
		
		//long startTime = System.nanoTime();
		//System.out.println("EXECUTION TIME: " +  ((System.nanoTime() - startTime)/1000000000) + " seconds." );
	}

	private HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> createBlocks(
			HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> kKgClassInstanceLabels) {
		HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> blocks = new HashMap<>();
		System.out.println("Start creating blocks...");
		//for each k
		for (String k : kKgClassInstanceLabels.keySet()) {
			//for each kgClass
			for (String kgClass : kKgClassInstanceLabels.get(k).keySet()) {
				//for each instance
				for (String uri : kKgClassInstanceLabels.get(k).get(kgClass).keySet()) {
					//for each label
					for (String label : kKgClassInstanceLabels.get(k).get(kgClass).get(uri)) {
						//tokenize label on any whitespace characters
						for (String token : label.split("\\s+")) {
							//check if token already exists in blocks
							if (blocks.containsKey(token)) {
								//check if block for token contains k
								if (blocks.get(token).containsKey(k)) {
									//check if block for token and k contains kgClass 
									if (blocks.get(token).get(k).containsKey(kgClass)) {
										//add URI to block
										blocks.get(token).get(k).get(kgClass).add(uri);
									} else {//!blocks.get(token).get(k).containsKey(kgClass) -> create new hashmap for kgclass
										HashSet<String> uriSet = createHashSet(uri);
										blocks.get(token).get(k).put(kgClass, uriSet);
									}					
								} else {//!blocks.get(token).containsKey(k) -> create new hashmap for k
									HashMap<String, HashSet<String>> kgClassWithURI = createHashMapWithHashSet(kgClass, uri);
									blocks.get(token).put(k, kgClassWithURI);
								}
							} else {//!blocks.containsKey(token) -> create new block
								HashMap<String, HashMap<String, HashSet<String>>> kKGClassWithURI = createDoubleHashMapWithHashSet(k, kgClass, uri);
								blocks.put(token,kKGClassWithURI);
							}
						}//end for each token
					}//end for each label
				}//end for each instance URI
			}//end for each kgClass		
		}//end for each k
		System.out.println(blocks.size() + " blocks created.");
		return blocks;
	}
	/*
	 * Create a new HashMap<String, HashMap<String, HashSet<String>>> with the params
	 * @param k1 key for the top HashMap
	 * @param k2 key for the second HashMap 
	 * @param initValue for the HashSet
	 */
	private HashMap<String, HashMap<String, HashSet<String>>> createDoubleHashMapWithHashSet(
			String k1, String k2, String initValue) {
		HashMap<String, HashMap<String, HashSet<String>>> map1 = new HashMap<>();
		HashMap<String, HashSet<String>> map2 = createHashMapWithHashSet(k2, initValue);
		map1.put(k1, map2);
		return map1;
	}

	/*
	 * Create a new HashSet<String>> with the init value
	 * @param initValue for the HashSet
	 */
	private HashSet<String> createHashSet(String initValue) {
		HashSet<String> set = new HashSet<>();
		set.add(initValue);
		return set;
	}

	/*
	 * Create a new HashMap<String, HashSet<String>> with the params
	 * @param key for the HashMap
	 * @param initValue for the HashSet
	 */
	private HashMap<String, HashSet<String>> createHashMapWithHashSet(
			String key, String initValue) {
		HashMap<String, HashSet<String>> map = new HashMap<>();
		HashSet<String> set = createHashSet(initValue);
		map.put(key, set);	
		return map;
	}

	private void getMatchedStringPairs(
			CountStringSimilarityResults results,
			HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> kKgClassInstanceLabels,
			StringMeasures stringMeasures, 
			ArrayList<Double> thresholds,
			HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> blocks) {
		boolean getPairs = true;
		if (stringMeasures.checkTFIDF()) {
			stringMeasures.trainTFIDF(kKgClassInstanceLabels);
		}
		//for each kg
		for (String fk : kKgClassInstanceLabels.keySet()) {
			switch (fk) {
				case "d":
					System.out.println("Start comparing D2Y");
					comparefKtK(fk, "y", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with D2Y. Start comparing D2W");
					comparefKtK(fk, "w", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with D2W. Start comparing D2O");
					comparefKtK(fk, "o", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with D2O. Start comparing D2N");
					comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with D2N");
					break;
				case "y":
					System.out.println("Start comparing Y2W");
					comparefKtK(fk, "w", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with Y2W. Start comparing Y2O");
					comparefKtK(fk, "o", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with Y2O. Start comparing Y2N");
					comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with Y2N");
					break;
				case "w":
					System.out.println("Start comparing W2O");
					comparefKtK(fk, "o", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with W2O. Start comparing W2N");
					comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with W2N");
					break;
				case "o":
					System.out.println("Start comparing O2N");
					comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs, blocks);
					System.out.println("Done with O2N");
					break;
			}
		}
		
		
	}

	/*private void getMatchedStringCounts(
			CountStringSimilarityResults results, HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> kKgClassInstanceLabels,
			StringMeasures stringMeasures, ArrayList<Double> thresholds) {
		boolean getPairs = false;
		//HashMap<String, HashMap<String, Integer>> results = new HashMap<String, HashMap<String, Integer>>();	
		//train TFIDF model for each KG class
		if (stringMeasures.checkTFIDF()) {
			stringMeasures.trainTFIDF(kKgClassInstanceLabels);
		}
		//for each kg
		for (String fk : kKgClassInstanceLabels.keySet()) {
			switch (fk) {
				case "d":
					comparefKtK(fk, "y", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
					comparefKtK(fk, "w", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
					comparefKtK(fk, "o", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
					comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);					
					break;
				case "y":
					comparefKtK(fk, "w", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
					comparefKtK(fk, "o", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
					comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
					break;
				case "w":
		//			comparefKtK(fk, "o", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
		//			comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);					
					break;
				case "o":
					comparefKtK(fk, "n", results, kKgClassInstanceLabels, stringMeasures, thresholds, getPairs);
					break;
				
				}
			}
				
		//return results;
	}*/

	private void comparefKtK(String fk, String tk,
			CountStringSimilarityResults results, 
			HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> kKgClassInstanceLabels,
			StringMeasures stringMeasures, 
			ArrayList<Double> thresholds, 
			boolean getPairs, 
			HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> blocks) {
		// pairs <SimMeasure, threshold>, <fromURI, toURI>>
		HashMap<Pair<String,Double>, HashSet<Pair<String, String>>> kgClassInstancePairResults = new HashMap<Pair<String, Double>, HashSet<Pair<String, String>>>();
		//for each kgClass
		//long startTime = System.nanoTime();
		for (String kgClass :kKgClassInstanceLabels.get(fk).keySet()) {
			
			//init 
			for (String simMeasure : stringMeasures.getUsedMeasures()) {
				for (Double t : thresholds) {
					HashSet<Pair<String, String>> emptySet = new HashSet<Pair<String,String>>();
					Pair<String, Double> k = new ImmutablePair<String, Double>(simMeasure, t);
					kgClassInstancePairResults.put(k, emptySet);
				}
			}
			
			if (kKgClassInstanceLabels.get(tk) != null) {				
				//for each kg class in other kg
				
				for (String toKgClass : kKgClassInstanceLabels.get(tk).keySet()) {
					System.out.println("Comparing " + fk +": " + kgClass + " with " + tk + ": " + toKgClass);
					//compare instanceLabels
					
					//BLOCKING
					//for each block
					int blockCounter = 0;
					//blocks: HashMap<token, <HashMap<k, HashMap<kgClass, <HashSet<URIs>>>>>>
					for (String block : blocks.keySet()) {
						//check if block contains fk and tk
						if (blocks.get(block).containsKey(fk) && blocks.get(block).containsKey(tk)) {
							if (blocks.get(block).get(fk).containsKey(kgClass) && blocks.get(block).get(tk).containsKey(toKgClass)) {
								HashMap<String, HashSet<String>> entries1 = getInstaceLabelsFromBlocks(blocks.get(block).get(fk).get(kgClass),
										kKgClassInstanceLabels.get(fk).get(kgClass));
								HashMap<String, HashSet<String>> entries2 = getInstaceLabelsFromBlocks(blocks.get(block).get(tk).get(toKgClass),
										kKgClassInstanceLabels.get(tk).get(toKgClass));
								
								
								entries1.entrySet()
								.stream()
								.parallel()
								.forEach(instanceWithLabels -> {compareLabelsWithOtherKG(results, 
																	fk, kgClass, instanceWithLabels, tk, 
																	entries2,
																	//kKgClassInstanceLabels.get(tk).get(toKgClass), 
																	toKgClass, stringMeasures, thresholds, 
																	kgClassInstancePairResults, getPairs);
								});
								
							} //block does not contain kgClass or toKgClass (or both)
						} //block does not contain fk or tk (or both)
						blockCounter++;
						if (blockCounter % (blocks.size()/10) == 0) {
							System.out.println(blockCounter + " of " + blocks.size() + " blocks processed.");
						}
					}//done with blocking
					
					//no blocking
					/*kKgClassInstanceLabels.get(fk).get(kgClass).entrySet()
						.stream()
						.parallel()
						.forEach(instanceWithLabels -> {compareLabelsWithOtherKG(results, 
															fk, kgClass, instanceWithLabels, tk, 
															kKgClassInstanceLabels.get(tk).get(toKgClass), 
															toKgClass, stringMeasures, thresholds, 
															kgClassInstancePairResults, getPairs);
						});
					
					*/
					/*
					for (Entry<String, HashSet<String>> instanceWithLabels : kKgClassInstanceLabels.get(fk).get(kgClass).entrySet()) {
						
						//to kg
						if(kKgClassInstanceLabels.get(tk) != null) {
							compareLabelsWithOtherKG(results, fk, kgClass, instanceWithLabels, tk, kKgClassInstanceLabels.get(tk), stringMeasures, thresholds, kgClassInstancePairResults, getPairs);
						}
						counter +=1;
						if (counter % 1000 == 0) {
							System.out.println(counter + " instances compared for " + fk + "2" + tk + ":" + kgClass + " in  " + ((System.nanoTime() - startTime)/1000000000) + " seconds.");
							startTime = System.nanoTime();
						}
					}
					*/
					//save results to disk
					//System.out.println(kgClassInstancePairResults);
					saveInstancePairResultsToDisk(fk, tk, kgClass, toKgClass, kgClassInstancePairResults, stringMeasures);
					kgClassInstancePairResults.clear();
				}
			}
		}
	
	}
		
		/*
		 * Return all instances with all labels that are in the block
		 * @param hashSet with uris in the block
		 * @param hashMap with all uris and labels
		 */
		private HashMap<String, HashSet<String>> getInstaceLabelsFromBlocks(
			HashSet<String> urisInBlock, 
			HashMap<String, HashSet<String>> allURIsWithLabels) {
			
			HashMap<String, HashSet<String>> results = new HashMap<>();
			for (String uriInBlock : urisInBlock) {
				if (allURIsWithLabels.containsKey(uriInBlock)) {
					results.put(uriInBlock, allURIsWithLabels.get(uriInBlock));
				//} else {
				//	System.out.println(uriInBlock + " is not in allURIsWithLabels");
				}
			}		
		return results;
	}

		private void saveInstancePairResultsToDisk(String fk, 
				String tk,
			String kgClass,
			String toKgClass, 
			HashMap<Pair<String, Double>, 
			HashSet<Pair<String, String>>> kgClassInstancePairResults,
			StringMeasures stringMeasures) {
			for (Pair<String, Double> simMeasurePair : kgClassInstancePairResults.keySet()) {
				//String threshold = getThreshold(simMeasure, stringMeasures);//1.0 for exact match
				String simMeasure = simMeasurePair.getLeft();
				Double threshold = simMeasurePair.getRight();
				Path savePath = Paths.get("./simMeasureResults/"+fk+"2"+tk+"_"+kgClass+"_"+toKgClass+"_"+simMeasure+"_"+threshold+".tsv");
				savePairsToDisk(savePath, kgClassInstancePairResults.get(simMeasurePair));
				/*switch (fk) {
					case "d":
						savePath = Paths.get("/Users/curtis/SeminarPaper_KG_files/DBpedia/d2"+ tk + "/"+simMeasure+"/"+threshold+"/"+kgClass+".tsv");
						savePairsToDisk(savePath, kgClassInstancePairResults.get(simMeasurePair));
						break;
					case "y":
						savePath = Paths.get("/Users/curtis/SeminarPaper_KG_files/YAGO/y2"+ tk + "/"+simMeasure+"/"+threshold+"/"+kgClass+".tsv");
						savePairsToDisk(savePath, kgClassInstancePairResults.get(simMeasurePair));
						break;
					case "o":
						savePath = Paths.get("/Users/curtis/SeminarPaper_KG_files/OpenCyc/o2"+ tk + "/"+simMeasure+"/"+threshold+"/"+kgClass+".tsv");
						savePairsToDisk(savePath, kgClassInstancePairResults.get(simMeasurePair));
						break;
				}*/
			}
			
		
	}

	private void savePairsToDisk(Path savePath,
				HashSet<Pair<String, String>> pairSet) {
			if (!pairSet.isEmpty()) {	
				try {
					BufferedWriter out = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
					
					for (Pair<String, String> p : pairSet) {
						String s = p.getLeft() + "\t" + p.getRight() + "\n";	
						out.write(s);
					}
					out.close();
					//System.out.println(pairSet.size() + " line(s) written to " + savePath.toString());
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
			
		}

	/*private String getThreshold(String simMeasure,
				StringMeasures stringMeasures) {
		Double t = 1.0;
			switch(simMeasure) {
				case "jaccard":
					t = stringMeasures.getJaccardT();
					break;
				case "jaro":
					t =  stringMeasures.getJaroT();
					break;
				case  "scaledLevenstein":
					t = stringMeasures.getScaledLevensteinT();
					break;
				case "tfidf":
					t = stringMeasures.getTfidfT();
					break;
				case "jaroWinkler":
					t = stringMeasures.getJaroWinklerT();
					break;
				case "softTfidf":
					t = stringMeasures.getSoftTfidfT();
					break;
			}
			return t.toString();
	}*/

	private void compareLabelsWithOtherKG(CountStringSimilarityResults results, 
			String fK, 
			String fromKgClass, 
			Entry<String, HashSet<String>> instanceWithLabels,
			String tK, 
			HashMap<String, HashSet<String>> otherKGinstancesWithLabels,
			String toKgClass, 
			StringMeasures stringMeasures, 
			ArrayList<Double> thresholds, 
			HashMap<Pair<String, Double>, HashSet<Pair<String, String>>> kgClassInstancePairResults, 
			boolean getPairs) {	
		
		String fromURI = instanceWithLabels.getKey();
		HashSet<String> labels = instanceWithLabels.getValue();
		
		//simResults <SimMeasure, threshold>, match>
		HashMap<Pair<String, Double>, Boolean> simResults = new HashMap<Pair<String,Double>, Boolean>();	
		
				//for each instance in other kg
				//System.out.println(fK + "_" + fromKgClass + " to " + tK + "_" + toKgClass);
				for (Entry<String, HashSet<String>> otherKGinstanceWithLabels : otherKGinstancesWithLabels.entrySet()) {
					String toURI = otherKGinstanceWithLabels.getKey();
					// instanceResults<SimMeasure, threshold>, booleanMatch>
					HashMap<Pair<String, Double>, Boolean> instanceResults = stringMeasures.getBlankInstanceResultsContainer(thresholds);
					
						
					// for each label in fromKG
					labelloop:
					for (String label : labels) {
						if (label != null && !label.equals("null")) {
						for (String otherLabel : otherKGinstanceWithLabels.getValue()) {
							if (otherLabel != null && !otherLabel.equals("null")) {
							
								//System.out.println(label + " AND " + otherLabel);
								//System.out.println(stringMeasures.getSimilarityScores(label, otherLabel));
								
								simResults = stringMeasures.getSimilarityResult(label, otherLabel, thresholds);
								//if (simResults.get("softTfidf"))
								//	System.out.println(label + " and " + otherLabel + ": "+ simResults);

								//update instanceResults
								instanceResults = updateInstanceResults(instanceResults, simResults);
								

								//check if all sim measures are true: break loop 
								if (!instanceResults.containsValue(false)) {
									//System.out.println("break loop: all labels are matched");
									break labelloop;
								}
								
							}
						}
					}
					
				}
				//create pair
				Pair<String, String> uriPair = new ImmutablePair<String, String>(fromURI, toURI);
				//check if at least one label matches
				for (Pair<String, Double> simMeasureP : instanceResults.keySet()) {
					//check if match is true
					if(instanceResults.get(simMeasureP)) {
						//check which similarity measure
						if (simMeasureP.getLeft().equals(jaccardS)) {
							//results.addInstanceCount(fK, fromKgClass, tK, toKgClass, jaccardS, simMeasureP.getRight());
							if(getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						} else if (simMeasureP.getLeft().equals(jaroS)) {
							//results.addInstanceCount(fK, fromKgClass, tK, toKgClass, jaroS, simMeasureP.getRight());
							if(getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						} else if (simMeasureP.getLeft().equals(scaledLevensteinS)) {
							//results.addInstanceCount(fK, fromKgClass, tK, toKgClass, scaledLevensteinS, simMeasureP.getRight());
							if(getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						} else if (simMeasureP.getLeft().equals(tfidfS)) {
							//results.addInstanceCount(fK, fromKgClass, tK, toKgClass, tfidfS, simMeasureP.getRight());
							if(getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						} else if (simMeasureP.getLeft().equals(jaroWinklerS)) {
							//results.addInstanceCount(fK, fromKgClass, tK, toKgClass, jaroWinklerS, simMeasureP.getRight());
							if(getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						} else if (simMeasureP.getLeft().equals(exactMatchS)) {
							//results.addInstanceCount(fK, fromKgClass, tK, toKgClass, exactMatchS, simMeasureP.getRight());
							if(getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						} else if (simMeasureP.getLeft().equals(allS)) {
							if (getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						} else if (simMeasureP.getLeft().equals(softTfidfS)) {
							//results.addInstanceCount(fK, fromKgClass, tK, toKgClass, softTfidfS, simMeasureP.getRight());
							if(getPairs && kgClassInstancePairResults.containsKey(simMeasureP)) {
								kgClassInstancePairResults.get(simMeasureP).add(uriPair);
							}
						}
					}
				}
			}		
		
	}


	

	private HashMap<Pair<String, Double>, Boolean> updateInstanceResults(
			HashMap<Pair<String, Double>, Boolean> instanceResults, HashMap<Pair<String, Double>, Boolean> simResults) {
		// get all string similarity measures
		for (Pair<String, Double> instanceResultS : instanceResults.keySet()) {
			//if string sim measure is not true
			if (!instanceResults.get(instanceResultS)) {
				//update if simResult is true
				if (simResults.get(instanceResultS)) {
					instanceResults.put(instanceResultS, true);
				}
			}
		}
		return instanceResults;
	}

	private HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> getInstanceLabels(
			HashMap<String, ArrayList<String>> classMap) {
		HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> instanceLabels = new HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>();
		
		for (String k : classMap.keySet()) {
			
			if (k.equals("d") || k.equals("y") || k.equals("w") ||k.equals("o") || k.equals("n")) {
			
			    for (String kgClass : classMap.get(k)) {
			    	//System.out.println(kgClass);
			    	//get all instance labels for the kgClass and save them in the instanceLabels object
			    	
			    	HashMap<String, HashSet<String>> instanceLabelsForKgClass= getInstanceLabelsForKgClass(k, kgClass);			    	 
			    	if (instanceLabels.containsKey(k)) {
			    		instanceLabels.get(k).put(kgClass, instanceLabelsForKgClass);
			    	} else {
			    		HashMap<String, HashMap<String, HashSet<String>>> instanceLabelsForSingleKgClass = new HashMap<String, HashMap<String,HashSet<String>>>();
			    		instanceLabelsForSingleKgClass.put(kgClass, instanceLabelsForKgClass);
			    		instanceLabels.put(k, instanceLabelsForSingleKgClass);
			    	}			    			    	
			    }
			}
		}
		return instanceLabels;
	}

	private HashMap<String, HashSet<String>> getInstanceLabelsForKgClass(
			String k, String kgClass) {
		HashMap<String, HashSet<String>> instanceLabelsForSingleKgClass = new HashMap<String, HashSet<String>>();
		
		//get file paths 
		Path filePath = null;	
		filePath = Paths.get("./InstanceLabels/");
		instanceLabelsForSingleKgClass = readFile(filePath, k, kgClass);
		return instanceLabelsForSingleKgClass;
	}

	private HashMap<String, HashSet<String>> readFile(Path filePath,
			String k,
			String kgClass) {
		HashMap<String, HashSet<String>> instanceLabelsForSingleKgClass = new HashMap<String, HashSet<String>>();
		Path fileName = Paths.get(filePath + "/" + k + "_" + kgClass + "InstancesWithLabels.txt");
		try (Stream<String> stream = Files.lines(fileName)) {
			stream.forEach(line -> addLineToHashMap(line, kgClass, instanceLabelsForSingleKgClass));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instanceLabelsForSingleKgClass;
	}

	private static void addLineToHashMap(String line, String kgClass,
			HashMap<String, HashSet<String>> instanceLabelsForSingleKgClass) {
		String[] words = line.split("\\t");
		HashSet<String> allLabels = new HashSet<String>();
		for (int i = 1; i < words.length; i++) {
			allLabels.add(words[i]);
			//System.out.println(words[i]);
		}
		
		instanceLabelsForSingleKgClass.put(words[0], allLabels);		
	}

}
