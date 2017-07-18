# Knowledge Graph Analysis
Code accompanying our paper "One Knowledge Graph to Rule them All? Analyzing the Differences between DBpedia, YAGO, Wikidata & co."

Quantitative analysis of the following Knowledge Graphs (KGs):
* DBpedia (D)
* YAGO (Y)
* Wikidata (W)
* NELL (N)
* OpenCyc (O)

## Approach:
* Get top 10 classes for each KG
* Calculation of class indegree and outdegree
* Get all instances for each class
* Calculation of minimum, average, median, and maximum indegree and outdegree for the instances of each class
* Create a combined list with all top 10 classes and equal classes in other KGs (e.g. with owl:sameAs properties)
* Calculate all degree values for the new classes as well
* Calculate the instance overlap of the classes using different string similarity measures

## Instructions:
1. **/LinkedInstances/*.py** creates files with all linked instances between two KGs.
    * Input:
        * KG files containing instances and/or links to other instances.
    * Output:
        * Files containing the combined links between two KGs (e.g. *DO_sameAs_union.nt* for the links between DBpedia and OpenCyc) that are denoted as **#o1**.
        * Move those **#o1** files to the */InstanceOverlap/owlSameAs/* folder.
2. **/GetInstances/src/GetInstances.java** creates files that contain all instances of a class including all English labels.
    * Input:
        * Array with class names for each KG.
        * Full KG or just the files containing the instances and labels.
    * Output:
        * Textfiles containing all instances with all English labels for each class in each KG.
        * Saved as *<k_className>InstancesWithLabels.txt* where *k* stands for the abbreviation of the KG (e.g. *d_ActorInstancesWithLabels.txt* for the actor instances in DBpedia). All those files are denoted as **#o2**.
        * Move these **#o2** files to the */InstanceOverlap/InstanceLabels/* folder.
3. **/InstanceOverlap/src/InstanceOverlapMain.java** executes the following three steps for each class in the className array for calculating the estimated overlap:
    1. **CountSameAs.java** creates files with the linked instances of two classes by e.g. using the *owl:sameAs* property.
        * Input: 
            * Class name.
            * **#o1** files with the linked instances in the */InstanceOverlap/owlSameAs/* folder.
            * **#o2** files with all English instance labels for the respective class and for each KG in the */InstanceOverlap/InstanceLabels/* folder.
        * Output:
            * Links between instances for each class1-class2 combination that is used as gold standard (there might be multiple classes that describe the same concept in a single KG, e.g. wordnet_actor_109765278 and wordnet_actor_109767197 in the YAGO KG). These files are saved as *<className1_className2>.tsv* in the */InstanceOverlap/owlSameAs/x2y/* folder (e.g. *Actor_wordnet_actor_109765278.tsv* in the *d2y* folder). These files are denoted as **#o3**.
    2. **CountStringSimilarity.java** creates files that contain all found links between two classes using the different string similarity measures (e.g. Jaro, Levenshtein) and different thresholds.
        * Input:
            * Class name.
            * **#o2** files.
        * Output:
            * Links between the instances of two classes that are found using a specific similarity measure and threshold. The results are saved as *<fromK_2_toK_fromClass_toClass_simMeasure_threshold>.tsv* in the */InstanceOverlap/simMeasureResults/* folder (e.g. *d2y_Actor_wordnet_actor_109765278_jaro_1.0.tsv*). These files are denoted as **#o4**.
    3. **EstimatedInstanceOverlap.java**
        * Input:
            * Class name.
            * **#o3** containing linked instances that is used as gold standard.
            * **#o4** containing the instances that should be linked based on the respective similarity measure and threshold.
        * Output:
            * *estimatedOverlap_<className_parameter_timestamp>.csv* files in the */InstanceOverlap/estimatedOverlap/* folder containing instance counts, precision, recall, f-measure, estimatedOverlap, number of links, count of matching alignment, count of partial matching alignment, and true positives for each class1-class2 combination for each class and each KG combination (e.g. *estimatedInstanceOverlap_Actor_wBlockingMax1000000_tokenBk4_2017_02_17_13_35_52.csv*).
    
