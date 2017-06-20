# Knowledge Graph Analysis
Code accompanying our paper "One Knowledge Graph to Rule them All? Analyzing the Differences between DBpedia, YAGO, Wikidata & co."

Quantitative analysis of the following Knowledge Graphs (KG):
* DBpedia
* YAGO
* Wikidata
* NELL
* OpenCyc

Approach:
* Get top 10 classes for each KG
* Calculation of class indegree and outdegree
* Get all instances for each class
* Calculation of minimum, average, median, and maximum indegree and outdegree for the instances of each class
* Create a combined list with all top 10 classes and equal classes in other KGs (e.g. with owl:sameAs properties)
* Calculate all degree values for the new classes as well
* Calculate the instance overlap of the classes using different string similarity measures
