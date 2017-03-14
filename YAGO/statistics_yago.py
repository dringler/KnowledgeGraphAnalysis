#KG, #triples, #nodes, #properties, #classes, avgIndegree, medianIndegree, avgOutdegree, medianOutdegree
import os
import numpy
import operator

#readFile = 'yago3_entire.ttl'
rdfType = 'rdf:type'#'<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
owlClass = '<http://www.w3.org/2002/07/owl#Class>'
rdfsSubClassOf = 'rdfs:subClassOf'
lineProgress = 1000000

sTriples = 0
sNodes = set()
sProperties = set()
sClasses = set()
indegreeDict = dict()
outdegreeDict = dict()

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

def countTriple(s,p,o):
	global sTriples
	if ((isURI(s) or isBlankNode(s)) and isURI(p) and (isURI(o) or isBlankNode(o) or isLiteral(o))):
		sTriples += 1
	#else:
	#	print ('{} {} {}'.format(s,p,o))

def checkAndAddNode(n):
	global sNodes
	if(isURI(n) or isBlankNode(n)):
		if (not n in sNodes):
			sNodes.add(n)

def countNodes(s,o):
	checkAndAddNode(s)
	checkAndAddNode(o)

def countProperties(p):
	global sProperties
	if (isURI(p)):
		if (not p in sProperties):
			sProperties.add(p)

def countClasses(s,p,o):
	global sClasses
	if (p == rdfType):# and o==owlClass):
		if (not o in sClasses):
			sClasses.add(o)
	if (p == rdfsSubClassOf):
		if (not o in sClasses):
			sClasses.add(o)
		if (not s in sClasses):
			sClasses.add(s)

def addIndegree(o):
	global indegreeDict
	if (isURI(o) or isBlankNode(o)):
		if (o in indegreeDict):
			indegreeDict[o] += 1
		else:
			indegreeDict[o] = 1

def addOutdegree(s):
	global outdegreeDict
	if (isURI(s) or isBlankNode(s)):
		if (s in outdegreeDict):
			outdegreeDict[s] += 1
		else:
			outdegreeDict[s] = 1

def getAvg(d):
	return numpy.average(numpy.array(d.values()))

def getMedian(d):
	return numpy.median(numpy.array(d.values()))

def isFullLine(s,p,o):
	if (s is not None and p is not None and o is not None):
		return True
	return False

try:
	print('GET STATISTICS FOR YAGO')
	for file in os.listdir('./'):
		#print file
		if file.endswith('Taxonomy.ttl'):
			print ('reading file {}'.format(file))
			f = open(file, 'r')
			lineCounter = 0
			for line in f:
				#check prefix
				if (line.startswith('<')):
					#print line
					splittedLine = line.rstrip('\n').split()
					s, p, o = getSPO(splittedLine)
					if(isFullLine(s,p,o)):
						countTriple(s,p,o)
						countNodes(s,o)
						countProperties(p)
						countClasses(s,p,o)
						addIndegree(o)
						addOutdegree(s)
				lineCounter += 1
				if (lineCounter % lineProgress == 0):
					print ('{} million lines read'.format(lineCounter / 1000000))
			f.close()
			print('{} read successfully'.format(file))
			print('#triples: {}, #nodes: {}, #properties: {}, #classes: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}'.format(sTriples, len(sNodes), len(sProperties), len(sClasses), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict)))
	print('DONE')		
	print('#triples: {}, #nodes: {}, #properties: {}, #classes: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}'.format(sTriples, len(sNodes), len(sProperties), len(sClasses), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict)))
	#print sNodes
	#print sProperties
	#print sClasses
	fw = open('yagoNodes.csv', 'w')
	for n in sNodes:
		fw.write(n+'\n')
	fw.close()
except:
	print('ERROR')

