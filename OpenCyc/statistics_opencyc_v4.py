#KG, #triples, #nodes, #properties, #classes, avgIndegree, medianIndegree, avgOutdegree, medianOutdegree
import numpy
import operator

#readFile = '../../../SeminarPaper_KG_Files/OpenCyc/opencyc-latest_s.nt'
readFile = '../../../SeminarPaper_KG_Files/OpenCyc/opencyc-latest.nt'
rdfType = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
owlClass = '<http://www.w3.org/2002/07/owl#Class>'
owlThing = '<http://www.w3.org/2002/07/owl#Thing>'
lineProgress = 1000000

sTriples = 0
sNodes = set()
sNodesNamespace = set()
sProperties = set()
sClasses = set()
sInstances = set()
indegreeDict = dict()
outdegreeDict = dict()
instanceIndegreeDict = dict()
instanceOutdegreeDict = dict()

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

def isNamespaceURI(w):
	if (w.startswith('<http://sw.opencyc.org/concept/')):
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
	else:
		print ('{} {} {}'.format(s,p,o))

def checkAndAddNode(n):
	global sNodes
	global sNodesNamespace
	if(isURI(n) or isBlankNode(n)):
		if (not n in sNodes):
			sNodes.add(n)
	if (isNamespaceURI(n)):
		if (not n in sNodesNamespace):
			sNodesNamespace.add(n)

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
	if (p == rdfType and o==owlClass):
		if (not s in sClasses):
			sClasses.add(s)

def countInstances(s,p,o):
	global sInstances
	if(p==rdfType and o in sClasses):
		if (not s in sClasses):
			if (not s in sInstances):
				sInstances.add(s)

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

def addInstanceIndegree(o):
	global sInstances
	global instanceIndegreeDict
	if (o in sInstances):
		if (o in instanceIndegreeDict):
			instanceIndegreeDict[o] += 1
		else:
			instanceIndegreeDict[o] = 1

def addInstanceOutdegree(s):
	global sInstances
	global instanceOutdegreeDict
	if (s in sInstances):
		if (s in instanceOutdegreeDict):
			instanceOutdegreeDict[s] += 1
		else:
			instanceOutdegreeDict[s] = 1

def getAvg(d):
	return numpy.average(numpy.array(d.values()))

def getMedian(d):
	return numpy.median(numpy.array(d.values()))

try:
	print('GET STATISTICS FOR OPENCYC')
	f = open(readFile, 'r')
	lineCounter = 0
	for line in f:
		#print line
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
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
	print('First run complete.')
	print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}, avgInstanceIndegree: {}, medianInstanceIndegree: {}, avgInstanceOutdegree: {}, medianInstanceOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict), getAvg(instanceIndegreeDict), getMedian(instanceIndegreeDict), getAvg(instanceOutdegreeDict), getMedian(instanceOutdegreeDict)))
	f = open(readFile, 'r')
	lineCounter = 0
	for line in f:
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		countInstances(s,p,o)
		lineCounter += 1
		if (lineCounter % lineProgress == 0):
			print ('{} million lines read'.format(lineCounter / 1000000))
	f.close()
	print('Second run complete.')
	print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}, avgInstanceIndegree: {}, medianInstanceIndegree: {}, avgInstanceOutdegree: {}, medianInstanceOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict), getAvg(instanceIndegreeDict), getMedian(instanceIndegreeDict), getAvg(instanceOutdegreeDict), getMedian(instanceOutdegreeDict)))
	f = open(readFile, 'r')
	lineCounter = 0
	for line in f:
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		addInstanceIndegree(o)
		addInstanceOutdegree(s)
		lineCounter += 1
		if (lineCounter % lineProgress == 0):
			print ('{} million lines read'.format(lineCounter / 1000000))
	f.close()
	print('DONE')
	print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}, avgInstanceIndegree: {}, medianInstanceIndegree: {}, avgInstanceOutdegree: {}, medianInstanceOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict), getAvg(instanceIndegreeDict), getMedian(instanceIndegreeDict), getAvg(instanceOutdegreeDict), getMedian(instanceOutdegreeDict)))
	#print sNodes
	#print sProperties
	#print ('###############')
	#print ('#####')
	#print ('###############')
	#print sClasses
	#print (sorted(instanceIndegreeDict.items(), key=operator.itemgetter(1)))
except:
	print('ERROR')

