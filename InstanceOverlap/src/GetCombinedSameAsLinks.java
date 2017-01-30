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
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;


public class GetCombinedSameAsLinks {
	
	public static int copyCounter = 0;
	public static int bidirectionalLinksCounter = 0;
	
	public static void main(String[] args) {
		
		//result object: HashMap of Index with List that contains the linked (sameAs) instances
		//ListIndex: 0:d, 1:y 2:o 3:w 4:n 5:wikipedia
		//static HashMap<Integer, List<String>> allSameAsLinks;
		HashSet<List<String>> allSameAsLinks = new HashSet<List<String>>();
		HashSet<List<String>> allSameAsLinksCopies = new HashSet<List<String>>();
		
		HashSet<String> addedInstances0 = new HashSet<String>();
		HashSet<String> addedInstances1 = new HashSet<String>();
		HashSet<String> addedInstances2 = new HashSet<String>();
		HashSet<String> addedInstances3 = new HashSet<String>();
		HashSet<String> addedInstances4 = new HashSet<String>();
		HashSet<String> addedInstances5 = new HashSet<String>();
		
		boolean yago = false;
		
		Path d2yPath = Paths.get("/Users/curtis/SeminarPaper_KG_files/DBpedia/owlSameAsFiles/yago_links.nt");
		//<http://dbpedia.org/resource/!!!> <http://www.w3.org/2002/07/owl#sameAs> <http://yago-knowledge.org/resource/!!!> .
		
		addInstances(allSameAsLinks, allSameAsLinksCopies, d2yPath, 0, addedInstances0, 1, addedInstances1, true, yago);
		
		Path d2oPath = Paths.get("/Users/curtis/SeminarPaper_KG_files/DBpedia/owlSameAsFiles/opencyc_links.nt");
		//<http://dbpedia.org/resource/Árnessýsla> <http://www.w3.org/2002/07/owl#sameAs> <http://sw.cyc.com/concept/Mx4rvYiFOJwpEbGdrcN5Y29ycA> .
		addInstances(allSameAsLinks, allSameAsLinksCopies, d2oPath, 0, addedInstances0, 2, addedInstances2, false, yago);
		
		Path d2wPath = Paths.get("/Users/curtis/SeminarPaper_KG_files/DBpedia/owlSameAsFiles/wikidata_links.ttl");
		//<http://dbpedia.org/resource/Wales> <http://www.w3.org/2002/07/owl#sameAs> <http://www.wikidata.org/entity/Q25> .

		Path d2wikipediaPath = Paths.get("/Users/curtis/SeminarPaper_KG_files/DBpedia/owlSameAsFiles/wikipedia_links_en_foaf_isPrimaryTopicOf.ttl");
		//<http://dbpedia.org/resource/Pietro_II_Orseolo> <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> <http://en.wikipedia.org/wiki/Pietro_II_Orseolo> .

		yago = true;
		Path y2dPath = Paths.get("/Users/curtis/SeminarPaper_KG_files/YAGO/yagoDBpediaInstances.ttl");
		//<!!!>	owl:sameAs	<http://dbpedia.org/resource/!!!> .
		addInstances(allSameAsLinks, allSameAsLinksCopies, y2dPath, 1, addedInstances1, 0, addedInstances0, false, yago);
		
		Path y2wikipediaPath;
		
		yago = false;
		Path o2dPath = Paths.get("/Users/curtis/SeminarPaper_KG_files/OpenCyc/o2d_sameAsLinksOnly.nt");
		
		//System.out.println(allSameAsLinks.size());
		//System.out.println(allSameAsLinks);

		saveFileToDisk(allSameAsLinks);
	
		System.out.println("bidirectionalLinksCounter: " + bidirectionalLinksCounter);
		System.out.println("copyCounter: " + copyCounter);
		System.out.print("DONE");
		}
	/* 
	 * Read all lines from the file and add the instances to the result object
	 * @param path
	 * @param first index of the first element
	 * @param second index of the second element
	 */
	private static void addInstances(HashSet<List<String>> allSameAsLinks,
			HashSet<List<String>> allSameAsLinksCopies,
			Path path, 
			int firstListIndex, 
			HashSet<String> addedInstancesFirst, 
			int secondListIndex, 
			HashSet<String> addedInstancesSecond, 
			boolean firstRun, 
			boolean yago) {
		try (Stream<String> stream = Files.lines(path)) {
			stream.forEach(line -> readLine(line, allSameAsLinks, allSameAsLinksCopies, firstListIndex, addedInstancesFirst, secondListIndex, addedInstancesSecond, firstRun, yago));//firstInstances, secondListIndex, secondInstances));
			System.out.println(path + " read");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/*
	 * Add instances to the result
	 */
	private static void readLine(String line, 
			HashSet<List<String>> allSameAsLinks,
			HashSet<List<String>> allSameAsLinksCopies,
			int firstListIndex, 
			HashSet<String> addedInstancesFirst, 
			int secondListIndex, 
			HashSet<String> addedInstancesSecond, 
			boolean firstRun,
			boolean yago) {//HashMap<String, Integer> firstInstances, int secondListIndex, HashMap<String, Integer> secondInstances) {
		
		boolean createdCopy = false;
		//skip all comments and other unnecessary rows
		//check if line starts with "<"
		if(line.startsWith("<")) { //CHANGE FOR NELL
			
			//split line on whitespace characters: space for nt, tab for ttl
			String[] words = line.split("\\s+");
			//get both instances
			String firstInstanceURI =  words[0];
			if (yago) {
				firstInstanceURI = "<http://yago-knowledge.org/resource/" + firstInstanceURI.substring(1);
			}
			String secondInstanceURI = words[2];
			
			//check for firstRun
			if(firstRun) {
				//simply add all lines to the allSameAsLinks result object and add instances to sets
				addNewLine(allSameAsLinks, firstListIndex, firstInstanceURI, secondListIndex, secondInstanceURI, addedInstancesFirst, addedInstancesSecond);		
			} else {
				//check if firstInstanceURI was already added
				if(!addedInstancesFirst.contains(firstInstanceURI)) {
					//firstInstanceURI was not already added -> add new line
					addNewLine(allSameAsLinks, firstListIndex, firstInstanceURI, secondListIndex, secondInstanceURI, addedInstancesFirst, addedInstancesSecond);
				} else {
					//go through all lines (as instance can appear more than once) and add the secondInstanceURI to the secondListIndex if the firstInstanceURI matches
					for (List<String> instanceList : allSameAsLinks) {
						//check URI of firstInstance
						if(instanceList.get(firstListIndex).equals(firstInstanceURI)) {
							//set secondInstanceURI to List (sameAs): replace "none" string or copy result and add new line if secondInstanceURI was already set
							if (instanceList.get(secondListIndex).equals("none")) {
								instanceList.set(secondListIndex, secondInstanceURI);
							} else {//there already exists an entry for the secondListIndex
								//check if value for the secondInstanceURI is already contained (can appear for bi-directional links)
								if (instanceList.get(secondListIndex).equals(secondInstanceURI)) {
									//pair was already added due to bi-directional links
									System.out.println("bidirectional link found for " + firstInstanceURI + " - " + secondInstanceURI);
									bidirectionalLinksCounter += 1;
								} else {
									//add a copy of the instanceList with the new value for the secondListIndex to the CopySet
								//addNewLineToCopySet(allSameAsLinksCopies, instanceList, secondListIndex, secondInstanceURI);
									//add secondInstanceURI to addedInstanceSecond-set
								//addedInstancesSecond.add(secondInstanceURI);
								//System.out.println("copy created for " + secondInstanceURI);
									copyCounter += 1;
								//createdCopy = true;
								}
							}
							//add secondInstanceURI to addedInstanceSecond-set
							addedInstancesSecond.add(secondInstanceURI);
						}//end check if firstListIndex of instanceList equals firstInstanceURI: if not true, it is no the same instance -> readNext 						
					}//end for loop
		
					// if copies were created: add all copies to the allSameAsLinks object
					if (createdCopy) {
						//System.out.println("allSameAsLinks.size() before adding copies: " + allSameAsLinks.size());
						allSameAsLinks.addAll(allSameAsLinksCopies);
						//System.out.println(allSameAsLinks.size());
						allSameAsLinksCopies.clear();
					}
					
				}//end addedInstace 
				
			}//end else:firstRun
		}
		
	}
	private static void addNewLineToCopySet(HashSet<List<String>> allSameAsLinksCopies,
			List<String> instanceList, 
			int secondListIndex,
			String secondInstanceURI) {
		//create a copy of the list that
		List<String> instanceListCopy = new ArrayList<String>(instanceList);
		//change the secondInstanceURI
		instanceListCopy.set(secondListIndex, secondInstanceURI);
		//add list to allSameAsLinks
		allSameAsLinksCopies.add(instanceListCopy);
		
	}
	private static void addNewLine(HashSet<List<String>> allSameAsLinks,
			int firstListIndex, String firstInstanceURI, int secondListIndex,
			String secondInstanceURI, HashSet<String> addedInstancesFirst,
			HashSet<String> addedInstancesSecond) {
		//create new List
		List<String> instanceList = getBlankInstanceList(); 
		
		//set! instances to the specified index (not add!)
		instanceList.set(firstListIndex, firstInstanceURI);
		instanceList.set(secondListIndex, secondInstanceURI);
		//add list to allSameAsLinks
		allSameAsLinks.add(instanceList);	
		//add instances to sets
		addedInstancesFirst.add(firstInstanceURI);
		addedInstancesSecond.add(secondInstanceURI);
		
	}
	private static List<String> getBlankInstanceList() {
		List<String> blankInstanceList = new ArrayList<String>();
		blankInstanceList.add(0, "none");
		blankInstanceList.add(1, "none");
		blankInstanceList.add(2, "none");
		blankInstanceList.add(3, "none");
		blankInstanceList.add(4, "none");
		blankInstanceList.add(5, "none");
		return blankInstanceList;
	}
	
	private static void saveFileToDisk(HashSet<List<String>> allSameAsLinks) {
		Path fileName = Paths.get("/Users/curtis/SeminarPaper_KG_files/combinedSameAsLinks.tsv");
		try {
			BufferedWriter out = Files.newBufferedWriter(fileName, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
			
			for(List<String> i : allSameAsLinks) {
				if (i.size()>5) {
					out.write(i.get(0) + "\t" + i.get(1) + "\t" + i.get(2) + "\t" + i.get(3) + "\t" + i.get(4) + "\t" + i.get(5) + "\t . \n");
				} else {
					System.out.println("missing index while writing to disk");
				}
			}
		
			out.close();
			System.out.println(allSameAsLinks.size() + " line(s) written to " + fileName.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
