#KG, #triples, #nodes, #properties, #classes, avgIndegree, medianIndegree, avgOutdegree, medianOutdegree
import os
import numpy
import operator

#readFile = 'yago3_entire.ttl'
rdfType = 'rdf:type'#'<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
owlClass = '<http://www.w3.org/2002/07/owl#Class>'
rdfsSubClassOf = 'rdfs:subClassOf'
lineProgress = 1000000

sClasses = set()
sInstances = set()

def isLiteral(w):
	if (w.startswith('"')):
		return True
	return False

def isURI(w):
	if (w.startswith('<')):
		return True
	return False

def isBlankNode(w):
	if (w.startswith('_:')):
		return True
	return False

def getSPO(splittedLine):
	word_position = 0
	for word in splittedLine:
		if (word_position == 0):
			subj = word
		elif (word_position == 1):
			pred = word
		elif (word_position == 2):
			obj = word
		else:
			return subj, pred, obj
		word_position += 1
	return subj, pred, obj


def countClassesAndInstances(s,p,o):
	global sClasses
	global sInstances
	if (p == rdfType):# and o==owlClass):
		if (not o in sClasses):
			sClasses.add(o)
		if (not s in sInstances):
			sInstances.add(s)
	

def isFullLine(s,p,o):
	if (s is not None and p is not None and o is not None):
		return True
	return False

try:
	print('GET STATISTICS FOR YAGO')
	f = open('yagoTypes.ttl', 'r')
	lineCounter = 0
	for line in f:
		#check prefix
		if (line.startswith('<')):
			#print line
			splittedLine = line.rstrip('\n').split()
			s, p, o = getSPO(splittedLine)
			if(isFullLine(s,p,o)):
				countClassesAndInstances(s,p,o)
				
		lineCounter += 1
		if (lineCounter % lineProgress == 0):
			print ('{} million lines read'.format(lineCounter / 1000000))
	f.close()
	print('DONE')		
	print('#classes: {}, #instances: {}'.format(len(sClasses), len(sInstances)))
	#print('#triples: {}, #nodes: {}, #properties: {}, #classes: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}'.format(sTriples, len(sNodes), len(sProperties), len(sClasses), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict)))
except:
	print('ERROR')

