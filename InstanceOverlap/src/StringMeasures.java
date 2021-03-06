import com.wcohen.ss.*;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.tokens.SimpleTokenizer;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class StringMeasures {
	//boolean
	private boolean jaccard;
	private boolean jaro;
	private boolean scaledLevenstein;
	private boolean tfidf;
	private boolean jaroWinkler;
	private boolean mongeElkan;
	private boolean exactMatch;
	private boolean softTfidf;
	private boolean internalSoftTfidf;
	private boolean all;
	//classes
	private Jaccard jaccardC;
	private Jaro jaroC;
	private ScaledLevenstein scaledLevensteinC;
	private TFIDF tfidfC;
	private JaroWinkler jaroWinklerC;
	private MongeElkan mongeElkanC;
	private SoftTFIDF softTfidfC;
	private Jaccard softTfidfJaccardC;
	private JaroWinkler softTfidfJaroWinklerC;
	private ScaledLevenstein softTfidfScaledLevensteinC;
	//thresholds
	private double jaccardT;
	private double jaroT;
	private double scaledLevensteinT;
	private double mongeElkanT;
	private double tfidfT;
	private double jaroWinklerT;
	private double softTfidfT;
	private double internalSoftTfidfT;
	private List<Double> thresholdsH;
	private List<Double> thresholdsL;
	private List<Double> thresholdsJaccard;
	//strings
	private String jaccardS = "jaccard";
	private String jaroS = "jaro";
	private String scaledLevensteinS = "scaledLevenstein";
	private String tfidfS = "tfidf";
	private String jaroWinklerS ="jaroWinkler";
	private String mongeElkanS ="mongeElkan";
	private String exactMatchS = "exactMatch";
	private String softTfidfS = "softTfidf";
	private String internalSoftTfidfS; 
	private String allS = "all";
	//keys
	private Pair<String, Double> exactMatchPair = new ImmutablePair<String, Double>(this.exactMatchS, 1.0);
	private Pair<String, Double> jaccardPair1 = new ImmutablePair<String, Double>(this.jaccardS, 1.0);
	private Pair<String, Double> jaccardPair8 = new ImmutablePair<String, Double>(this.jaccardS, 0.8);
	private Pair<String, Double> jaccardPair6 = new ImmutablePair<String, Double>(this.jaccardS, 0.6);
	private Pair<String, Double> scaledLevensteinPair1 = new ImmutablePair<String, Double>(this.scaledLevensteinS, 1.0);
	private Pair<String, Double> scaledLevensteinPair9 = new ImmutablePair<String, Double>(this.scaledLevensteinS, 0.9);
	private Pair<String, Double> scaledLevensteinPair8 = new ImmutablePair<String, Double>(this.scaledLevensteinS, 0.8);
	private Pair<String, Double> mongeElkanPair1 = new ImmutablePair<String, Double>(this.mongeElkanS, 1.0);
	private Pair<String, Double> mongeElkanPair95 = new ImmutablePair<String, Double>(this.mongeElkanS, 0.95);
	private Pair<String, Double> mongeElkanPair9 = new ImmutablePair<String, Double>(this.mongeElkanS, 0.9);
	private Pair<String, Double> softTfidfPair1 = new ImmutablePair<String, Double>(this.softTfidfS, 1.0);
	private Pair<String, Double> softTfidfPair95 = new ImmutablePair<String, Double>(this.softTfidfS, 0.95);
	private Pair<String, Double> softTfidfPair9 = new ImmutablePair<String, Double>(this.softTfidfS, 0.9);
	private Pair<String, Double> jaroPair1 = new ImmutablePair<String, Double>(this.jaroS, 1.0);
	private Pair<String, Double> jaroPair95 = new ImmutablePair<String, Double>(this.jaroS, 0.95);
	private Pair<String, Double> jaroPair9 = new ImmutablePair<String, Double>(this.jaroS, 0.9);
	private Pair<String, Double> jaroWinklerPair1 = new ImmutablePair<String, Double>(this.jaroWinklerS, 1.0);
	private Pair<String, Double> jaroWinklerPair95 = new ImmutablePair<String, Double>(this.jaroWinklerS, 0.95);
	private Pair<String, Double> jaroWinklerPair9 = new ImmutablePair<String, Double>(this.jaroWinklerS, 0.9);
	
	private Pair<String, Double> allPair = new ImmutablePair<String, Double>(this.allS, 1.0);
	/**
	   * StringMeasures constructor
	   * @param exactMatch boolean
	   * @param jaccard boolean
	   * @param jaccardT threshold value (double)
	   * @param jaro boolean
	   * @param jaroT threshold value (double)
	   * @param scaledLevenstein boolean
	   * @param scaledLevensteinT threshold value (double)
	   * @param tfidf boolean
	   * @param tfidfT threshold value (double)
	   * @param jaroWinkler boolean
	   * @param jaroWinklerT threshold value (double)
	   * @param softTfidf boolean
	   * @param softTfidfT threshold value (double) 
	   * @param internalSoftTfidf  boolean: true if internal sim measure should be used for softTFIDF
	   * @param internalSoftTfidfS internal sim measure for softTFIDF ("jaroWinkler", "jaccard", or "scaledLevenstein")
	   * @param internalSoftTfidfT threshold value for internal sim measure of softTFIDF
	   */
	public StringMeasures(boolean exactMatch, boolean jaccard, double jaccardT, boolean jaro, double jaroT, boolean scaledLevenstein, double scaledLevensteinT, boolean mongeElkan, double mongeElkanT, boolean tfidf, double tfidfT, boolean jaroWinkler, double jaroWinklerT, boolean softTfidf, double softTfidfT, boolean internalSoftTfidf, String internalSoftTfidfS, double internalSoftTfidfT, boolean all) {		
		String config = "";
		this.exactMatch = exactMatch;
		if (exactMatch)
			config = "exactMatch, ";
		this.jaccard = jaccard;
		this.jaro = jaro;
		this.scaledLevenstein = scaledLevenstein;
		this.mongeElkan = mongeElkan;
		this.tfidf = tfidf;
		this.jaroWinkler = jaroWinkler;
		this.softTfidf = softTfidf;
		this.all = all;
		
		if (jaccard) {
			this.jaccardC = new Jaccard();
			this.jaccardT = jaccardT;
			config = config + "jaccard ("+ jaccardT+"), ";
		}
		if (jaro) {
			this.jaroC = new Jaro();
			this.jaroT = jaroT;
			config = config + "jaro ("+ jaroT+"), ";
		}
		if (scaledLevenstein) {
			this.scaledLevensteinC = new ScaledLevenstein();
			this.scaledLevensteinT = scaledLevensteinT;
			config = config + "scaledLevenstein ("+ scaledLevensteinT+"), ";
		}
		if (mongeElkan) {
			this.mongeElkanC = new MongeElkan();
			this.mongeElkanC.setScaling(true);
			this.mongeElkanT = mongeElkanT;
			config = config + "mongeElkan (" + mongeElkanT +"), "; 
		}
		if (tfidf) {
			this.tfidfC = new TFIDF();
			this.tfidfT = tfidfT;
			config = config + "tfidf ("+ tfidfT+"), ";
		}
		if (jaroWinkler) {
			this.jaroWinklerC = new JaroWinkler();
			this.jaroWinklerT = jaroWinklerT;
			config = config + "jaroWinkler ("+ jaroWinklerT+"), ";
		}
		if (softTfidf) {
			this.softTfidfT = softTfidfT;
			config = config + "softTfidf ("+ softTfidfT+"), ";
			this.internalSoftTfidf = internalSoftTfidf;
			if (internalSoftTfidf) {
				//set threshold and internal sim measure string
				this.internalSoftTfidfT = internalSoftTfidfT;
				this.internalSoftTfidfS = internalSoftTfidfS;
				//get internal sim measure
				if (internalSoftTfidfS.equals(this.jaccardS)) {
					this.softTfidfJaccardC = new Jaccard();
					this.softTfidfC = new SoftTFIDF(new SimpleTokenizer(true, true), this.softTfidfJaccardC , internalSoftTfidfT);
					config = config + "internalSoftTfidf:jaccard ("+ internalSoftTfidfT+"), ";
				} else if (internalSoftTfidfS.equals(this.jaroWinklerS)) {
					this.softTfidfJaroWinklerC = new JaroWinkler();
					this.softTfidfC = new SoftTFIDF(new SimpleTokenizer(true, true), softTfidfJaroWinklerC , internalSoftTfidfT);
					config = config + "internalSoftTfidf:jaroWinkler ("+ internalSoftTfidfT+"), ";
				} else if (internalSoftTfidfS.equals(this.scaledLevensteinS)) {
					this.softTfidfScaledLevensteinC = new ScaledLevenstein();
					this.softTfidfC = new SoftTFIDF(new SimpleTokenizer(true, true), softTfidfScaledLevensteinC , internalSoftTfidfT);
					config = config + "internalSoftTfidf:scaledLevenstein ("+ internalSoftTfidfT+"), ";
				} else {
					System.out.println("Internal SoftTFIDF sim measure not found. Please use jaccard, jaroWinkler or scaledLevenstein.");
				}
			} else { //use without internal sim measure
				this.softTfidfC = new SoftTFIDF();
			}
			
		}
		System.out.println("Configuration: " + config.substring(0, config.length()-2));
	}
	/**
	   * StringMeasures constructor
	   * exactMatch, jaccard, jaro, scaledLevenstein, jaroWinkler, softTfidf
	   * 
	   */
	public StringMeasures(ArrayList<Double> thresholdsH, ArrayList<Double> thresholdsL, ArrayList<Double> thresholdsJaccard) {
		this.thresholdsH = thresholdsH;
		this.thresholdsL = thresholdsL;
		this.thresholdsJaccard = thresholdsJaccard;
		
		this.exactMatch = true;
		this.jaccard = true;
		this.jaro = true;
		this.scaledLevenstein = true;
		this.jaroWinkler = true;
		this.mongeElkan = true;
		
		this.softTfidf = false;
		this.all = false;
		this.tfidf = false;
		this.internalSoftTfidf = false;
	
		this.jaccardC = new Jaccard();
		this.jaroC = new Jaro();
		this.scaledLevensteinC = new ScaledLevenstein();
		this.jaroWinklerC = new JaroWinkler();
		this.softTfidfC = new SoftTFIDF();
		this.mongeElkanC = new MongeElkan();
		mongeElkanC.setScaling(true);
	}
	
	public double getJaccardScore(String s1, String s2) {
		double score = 0.0;
		if (s1 == null || s2 == null) {
			return score;
		} else {	
			try {
				Jaccard j = new Jaccard();
				score = j.score(j.prepare(s1), j.prepare(s2));
				j = null;
				return score;
			} catch (NullPointerException e) {
				return score;		
			}
		}
	}
	public double getJaroScore(String s1, String s2) {
		double score = 0.0;
		if (s1 == null || s2 == null) {
			return score;
		} else {
			Jaro j = new Jaro();
			score = j.score(j.prepare(s1), j.prepare(s2));
			j = null;
			return score;
		}
	}
	public double getScaledLevenstein(String s1, String s2) {
		double score = 0.0; 
		if (s1 == null || s2 == null) {
			return score;
		} else {
			ScaledLevenstein sl = new ScaledLevenstein();
			score = sl.score(sl.prepare(s1), sl.prepare(s2));
			sl = null;
			return score;
		}
	}
	public double getJaroWinklerScore(String s1, String s2) {
		double score = 0.0;
		if (s1 == null || s2 == null) {
			return score;
		} else {
			JaroWinkler jw = new JaroWinkler();
			score = jw.score(jw.prepare(s1), jw.prepare(s2));
			jw = null;
			return score;
		}
	}
	public double getMongeElkanScore(String s1, String s2) {
		double score = 0.0;
		if (s1 == null || s2 == null) {
			return score;
		} else {
			MongeElkan me = new MongeElkan();
			me.setScaling(true);
			score = me.score(me.prepare(s1), me.prepare(s2));
			me = null;
			return score;
		}
	}
	public double getTfidfScore(String s1, String s2) {
		double score = 0.0;
		if (s1 == null || s2 == null) {
			return score;
		} else {
			TFIDF t = new TFIDF();
			score = t.score(t.prepare(s1), t.prepare(s2));
			t = null;
			return score;
		}
	}
	public double getSoftTfidfScore(String s1, String s2) {
		double score = 0.0;
		if (s1 == null || s2 == null) {
			return score;
		} else {
			SoftTFIDF st = new SoftTFIDF();
			score = st.score(st.prepare(s1), st.prepare(s2));
			st = null;
			return score;			
		}
	}
	
	/**
	   * Get string equality score
	   * @param s1
	   * @param s2
	   * @return double (1.0 if strings are equal; 0.0 otherwise)
	   */
	public double getExactMatchScore(String s1, String s2) {
		if (s1 == null || s2 == null) {return 0.0;}
		double score = 0.0;
		if (s1.equals(s2)) {
			score = 1.0;
		}
		return score;
	}
	public double getAllScore(String s1, String s2) {
		double score = 1.0;
		return score;
	}
	/**
	   * Check if two strings are equal
	   * @param s1
	   * @param s2
	   * @return boolean
	   */
	public boolean getExactMatch(String s1, String s2) {
		if (s1.equals(s2)) { 
			return true;
		}
		return false;
	}
	public boolean getAllMatch(String s1, String s2) {
		return true;
	}
	/**
	   * Get similarity scores for two strings s1 and s2
	   * @param s1
	   * @param s2
	   * @return HashMap<String, Double> with similarity measure name and result score
	   */
	public HashMap<String, Double> getSimilarityScores(String s1, String s2) {
		HashMap<String, Double> resultScores = new HashMap<String, Double>();
		if (this.exactMatch) {
			resultScores.put(this.exactMatchS, getExactMatchScore(s1, s2));
		}
		if (this.jaccard) {
			double score = 0.0;
			if (s1 != null && s2 != null) {
				score = getJaccardScore(s1, s2);
			}
			resultScores.put(this.jaccardS, score);
		}
		if (this.jaro) {
			resultScores.put(this.jaroS, getJaroScore(s1, s2));
		}
		if (this.scaledLevenstein) {
			resultScores.put(this.scaledLevensteinS, getScaledLevenstein(s1, s2));
		}
		if (this.jaroWinkler) {
			resultScores.put(this.jaroWinklerS, getJaroWinklerScore(s1, s2));
		}
		if (this.mongeElkan) {
			resultScores.put(this.mongeElkanS, getMongeElkanScore(s1, s2));
		}
		/*if (this.tfidf) {
			resultScores.put(this.tfidfS, getTfidfScore(s1, s2));
		}	
		if (this.softTfidf) {
			resultScores.put(this.softTfidfS, getSoftTfidfScore(s1, s2));
		}
		if (this.all) {
			resultScores.put(this.allS, getAllScore(s1,s2));
		}*/
		return resultScores;
	}
	/**
	   * Get similarity results for two strings s1 and s2
	   * @param s1
	   * @param s2
	 * @param thresholds H
	 * @param thresholdsL 
	   * @return HashMap<String, Boolean> with similarity measure name and result
	   */
	public HashMap<Pair<String, Double>, Boolean> getSimilarityResult(String s1, String s2, 
			ArrayList<Double> thresholdsH, ArrayList<Double> thresholdsLL) {
		HashMap<Pair<String, Double>, Boolean> results = new HashMap<Pair<String, Double>, Boolean>();
		if (this.exactMatch) {	
			results.put(getKeyPair(this.exactMatchS, 1.0), getExactMatch(s1, s2));
		}
		//if (this.all) {
		//	results.put(getKeyPair(this.allS, 1.0), getAllMatch(s1, s2));
		//}
		//check scores against thresholds
		HashMap<String, Double> resultScores = getSimilarityScores(s1, s2);
		//get all scores
		for (Entry<String, Double> entry : resultScores.entrySet()) {
			//for each threshold: check sim measure & check threshold
			for (Double t : thresholdsJaccard) {
				if (entry.getKey().equals(this.jaccardS)) {
					results.put(getKeyPair(this.jaccardS, t), checkThreshold(entry.getValue().doubleValue(), t));
				}
			}
			for (Double t : thresholdsL) {
				if (entry.getKey().equals(this.scaledLevensteinS)) {
					results.put(getKeyPair(this.scaledLevensteinS, t), checkThreshold(entry.getValue().doubleValue(), t));
				}
			}
			for (Double t : thresholdsH) {
				if (entry.getKey().equals(this.mongeElkanS)) {
					results.put(getKeyPair(this.mongeElkanS, t), checkThreshold(entry.getValue().doubleValue(), t));
				} else if (entry.getKey().equals(this.jaroS)) {
					results.put(getKeyPair(this.jaroS, t), checkThreshold(entry.getValue().doubleValue(), t));				
				} else if (entry.getKey().equals(this.jaroWinklerS)) {
					results.put(getKeyPair(this.jaroWinklerS, t), checkThreshold(entry.getValue().doubleValue(), t));				
				/*} else if (entry.getKey().equals(this.softTfidfS)) {
					results.put(getKeyPair(this.softTfidfS, t), checkThreshold(entry.getValue().doubleValue(), t));
				} else if (entry.getKey().equals(this.tfidfS)) {
					results.put(getKeyPair(this.tfidfS, t), checkThreshold(entry.getValue().doubleValue(), t));	
					*/
				}
			}
		}
		return results;
	}
	private Pair<String, Double> getKeyPair(String simMeasure, Double t) {
		switch(simMeasure) {
		case "exactMatch":
			return getExactMatchPair();
		case "all":
			return getAllMatchPair();
		case "jaccard":
			if (t == 1.0)
				return getJaccardPair1();
			else if (t == 0.8)
				return getJaccardPair8();
			else if (t == 0.6)
				return getJaccardPair6();
			break;
		case "scaledLevenstein":
			if (t == 1.0)
				return getScaledLevensteinPair1();
			else if (t == 0.9)
				return getScaledLevensteinPair9();
			else if (t == 0.8)
				return getScaledLevensteinPair8();
			break;
		case "mongeElkan":
			if (t == 1.0)
				return getMongeElkanPair1();
			else if (t == 0.95)
				return getMongeElkanPair95();
			else if (t == 0.9)
				return getMongeElkanPair9();
			break;
		case "jaro":
			if (t == 1.0)
				return getJaroPair1();
			else if (t == 0.95)
				return getJaroPair95();
			else if (t == 0.9)
				return getJaroPair9();
			break;	
		case "jaroWinkler":
			if (t == 1.0)
				return getJaroWinklerPair1();
			else if (t == 0.95)
				return getJaroWinklerPair95();
			else if (t == 0.9)
				return getJaroWinklerPair9();
			break;	
		/*case "softTfidf":
			if (t == 1.0)
				return getSoftTfidfPair1();
			else if (t == 0.95)
				return getSoftTfidfPair95();
			else if (t == 0.9)
				return getSoftTfidfPair9();
			break;*/
		}
		return null;
	}
	/**
	   * Check if value is at least the threshold (v>=t) 
	   * @return boolean
	   */
	private boolean checkThreshold(double v, double t) {
		boolean r = false;
		if (v>=t)
			r = true;	
		return r;
	}
	/**
	   * Get blank HashMap with all similarity measures 
	 * @param thresholdsH
	 * @param thresholdsL 
	   * @return HashMap
	   */
	public HashMap<Pair<String,Double>, Boolean> getBlankInstanceResultsContainer(ArrayList<Double> thresholdsH, 
			ArrayList<Double> thresholdsL,
			ArrayList<Double> thresholdsJaccard) {
		HashMap<Pair<String,Double>, Boolean> instanceResults = new HashMap<Pair<String, Double>, Boolean>();
		if (this.exactMatch) {
			instanceResults.put(getKeyPair(this.exactMatchS, 1.0), false);
		}
		if (this.all) {
			instanceResults.put(getKeyPair(this.allS, 1.0), false);
		}
		for (Double t : thresholdsJaccard) {
			if (this.jaccard) {
				instanceResults.put(getKeyPair(this.jaccardS, t), false);
			}
		}
		for (Double t : thresholdsL) {			
			if (this.scaledLevenstein) {
				instanceResults.put(getKeyPair(this.scaledLevensteinS, t), false);
			}		
		}
		for (Double t : thresholdsH) {
			if (this.jaro) {
				instanceResults.put(getKeyPair(this.jaroS, t), false);
			}		
			if (this.jaroWinkler) {
				instanceResults.put(getKeyPair(this.jaroWinklerS, t), false);
			}
			if (this.mongeElkan) {
				instanceResults.put(getKeyPair(this.mongeElkanS, t), false);
			}
			
			/*if (this.tfidf) {
				instanceResults.put(getKeyPair(this.tfidfS, t), false);
			}
			if (this.softTfidf) {
				instanceResults.put(getKeyPair(this.softTfidfS, t), false);
			}*/
		}
		return instanceResults;
	}
	/**
	   * Check if TFIDF or SoftTFIDF is used 
	   * @param kKgClassInstanceLabels 
	   * @return boolean
	   */
	public boolean checkTFIDF() {
		if (this.tfidf || this.softTfidf)
			return true;
		return false;
	}

	public void trainTFIDF(HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> kKgClassInstanceLabels
			//Collection<HashSet<String>> labels,
			//HashMap<String, HashMap<String, HashSet<String>>> toKgClasses
			) {
		System.out.println("Training TFIDF");
		if (this.internalSoftTfidf) {
			if (this.internalSoftTfidfS.endsWith(this.jaccardS)) {
				this.softTfidfJaccardC = new Jaccard();
				this.softTfidfC = new SoftTFIDF(new SimpleTokenizer(true, true), this.softTfidfJaccardC , this.internalSoftTfidfT);
			} else if (this.internalSoftTfidfS.equals(this.jaroWinklerS)) {
				this.softTfidfJaroWinklerC = new JaroWinkler();
				this.softTfidfC = new SoftTFIDF(new SimpleTokenizer(true, true), softTfidfJaroWinklerC , this.internalSoftTfidfT);
			} else if (this.internalSoftTfidfS.equals(this.scaledLevensteinS)) {
				this.softTfidfScaledLevensteinC = new ScaledLevenstein();
				this.softTfidfC = new SoftTFIDF(new SimpleTokenizer(true, true), softTfidfScaledLevensteinC , this.internalSoftTfidfT);
			}
		} else {
			this.softTfidfC = new SoftTFIDF();
		}
		
		this.tfidfC = new TFIDF();
		
		//train on labels
		Set<String> labels = getAllLabels(kKgClassInstanceLabels);
		System.out.println(labels.size() + " labels received in total.");
		Set<StringWrapper> labelsW = new HashSet<StringWrapper>(labels.size() + 10, 1f);
		//for each KG
		/*for (String fk : kKgClassInstanceLabels.keySet()) {
			//for each kg class
			for (String kgClass : kKgClassInstanceLabels.get(fk).keySet()) {
				for (String instance : kKgClassInstanceLabels.get(fk).get(kgClass).keySet()) {
					for (String label : kKgClassInstanceLabels.get(fk).get(kgClass).get(instance)) {
						labelsW.add(this.tfidfC.prepare(label));
					}
				}
				System.out.println("Training for " + fk + ": " + kgClass + " done.");
			}
		}*/
		labelsW = labels.stream()
					.parallel()
					.map(label -> this.tfidfC.prepare(label))
					.collect(Collectors.toSet());
		System.out.println(labelsW.size() + " labels as StringWrapper added. Start training TFIDF");
		if (this.softTfidf)
			this.softTfidfC.train(new BasicStringWrapperIterator(labelsW.iterator()));
		if (this.tfidf)
			this.tfidfC.train(new BasicStringWrapperIterator(labelsW.iterator()));
		
		System.out.println("TFIDF is trained on labels with collection size of " + this.softTfidfC.getCollectionSize());
		
	}
	private Set<String> getAllLabels(
			HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> kKgClassInstanceLabels) {
		//get number of labels
		int labelCounter = 0;
		for (String fk : kKgClassInstanceLabels.keySet()) {
			for (String kgClass : kKgClassInstanceLabels.get(fk).keySet()) {
				for (String instance : kKgClassInstanceLabels.get(fk).get(kgClass).keySet()) {
					labelCounter += kKgClassInstanceLabels.get(fk).get(kgClass).get(instance).size();
				}
			}
		}
		//init set and add all labels to the set
		HashSet<String> allLabels = new HashSet<String>(labelCounter + 10, 1f);
		for (String fk : kKgClassInstanceLabels.keySet()) {
			for (String kgClass : kKgClassInstanceLabels.get(fk).keySet()) {
				for (String instance : kKgClassInstanceLabels.get(fk).get(kgClass).keySet()) {
					for (String label : kKgClassInstanceLabels.get(fk).get(kgClass).get(instance)) {
						allLabels.add(label);
					}
				}
			}
		}
		
		return allLabels;
	}
	public double getJaccardT() {
		return jaccardT;
	}
	public double getJaroT() {
		return jaroT;
	}
	public double getScaledLevensteinT() {
		return scaledLevensteinT;
	}
	public double getMongeElkanT() {
		return mongeElkanT;
	}
	public double getTfidfT() {
		return tfidfT;
	}
	public double getJaroWinklerT() {
		return jaroWinklerT;
	}
	public double getSoftTfidfT() {
		return softTfidfT;
	}
	public double getInternalSoftTfidfT() {
		return internalSoftTfidfT;
	}
	public void setJaccardT(double jaccardT) {
		this.jaccardT = jaccardT;
	}
	public void setJaroT(double jaroT) {
		this.jaroT = jaroT;
	}
	public void setMongeElkanT(double mongeElkanT) {
		this.mongeElkanT = mongeElkanT;
	}
	public void setScaledLevensteinT(double scaledLevensteinT) {
		this.scaledLevensteinT = scaledLevensteinT;
	}
	public void setTfidfT(double tfidfT) {
		this.tfidfT = tfidfT;
	}
	public void setJaroWinklerT(double jaroWinklerT) {
		this.jaroWinklerT = jaroWinklerT;
	}
	public void setSoftTfidfT(double softTfidfT) {
		this.softTfidfT = softTfidfT;
	}
	public void setInternalSoftTfidfT(double internalSoftTfidfT) {
		this.internalSoftTfidfT = internalSoftTfidfT;
	}
	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}
	public void setAllMatch(boolean all) {
		this.all = all;
	}
	public List<String> getUsedMeasures() {
		List<String> usedMeasures = new ArrayList<String>();
		if (this.exactMatch)
			usedMeasures.add(this.exactMatchS);
		if (this.all)
			usedMeasures.add(this.allS);
		if (this.jaccard)
			usedMeasures.add(this.jaccardS);
		if (this.jaro)
			usedMeasures.add(this.jaroS);
		if (this.jaroWinkler)
			usedMeasures.add(this.jaroWinklerS);
		if (this.scaledLevenstein)
			usedMeasures.add(this.scaledLevensteinS);
		if (this.mongeElkan)
			usedMeasures.add(this.mongeElkanS);
		if (this.softTfidf)
			usedMeasures.add(this.softTfidfS);
		if (this.tfidf)
			usedMeasures.add(this.tfidfS);
		return usedMeasures;
	}

	public Pair<String, Double> getExactMatchPair() {
		return exactMatchPair;
	}
	public Pair<String, Double> getAllMatchPair() {
		return allPair;
	}
	//Low thresholds
	public Pair<String, Double> getJaccardPair1() {
		return jaccardPair1;
	}
	public Pair<String, Double> getJaccardPair8() {
		return jaccardPair8;
	}
	public Pair<String, Double> getJaccardPair6() {
		return jaccardPair6;
	}
	public Pair<String, Double> getScaledLevensteinPair1() {
		return scaledLevensteinPair1;
	}
	public Pair<String, Double> getScaledLevensteinPair9() {
		return scaledLevensteinPair9;
	}
	public Pair<String, Double> getScaledLevensteinPair8() {
		return scaledLevensteinPair8;
	}
	//high thresholds
	public Pair<String, Double> getMongeElkanPair1() {
		return mongeElkanPair1;
	}
	public Pair<String, Double> getMongeElkanPair9() {
		return mongeElkanPair95;
	}
	public Pair<String, Double> getMongeElkanPair95() {
		return mongeElkanPair9;
	}	
	public Pair<String, Double> getJaroPair1() {
		return jaroPair1;
	}
	public Pair<String, Double> getJaroPair9() {
		return jaroPair9;
	}
	public Pair<String, Double> getJaroPair95() {
		return jaroPair95;
	}
	public Pair<String, Double> getJaroWinklerPair1() {
		return jaroWinklerPair1;
	}
	public Pair<String, Double> getJaroWinklerPair9() {
		return jaroWinklerPair9;
	}
	public Pair<String, Double> getJaroWinklerPair95() {
		return jaroWinklerPair95;
	}
	public Pair<String, Double> getSoftTfidfPair1() {
		return softTfidfPair1;
	}
	public Pair<String, Double> getSoftTfidfPair9() {
		return softTfidfPair9;
	}
	public Pair<String, Double> getSoftTfidfPair95() {
		return softTfidfPair95;
	}


}
