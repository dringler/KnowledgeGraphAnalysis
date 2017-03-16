#KG, #triples, #nodes, #properties, #classes, avgIndegree, medianIndegree, avgOutdegree, medianOutdegree
import numpy
import operator


nellSample = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.esv_s.csv'
nell = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.esv.csv'
ontology = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.ontology.csv'

#rdfType = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
pMemberOfSets = 'memberofsets'
pGeneralizations = 'generalizations'
#owlClass = '<http://www.w3.org/2002/07/owl#Class>'
oClass = 'concept:rtwcategory'
pClass = 'rtwrelation'
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



def isNode(w):
	if (w.startswith('concept') or w.startswith('http:')):
		return True
	return False

def isNamespaceNode(w):
	if(w.startswith('concept')):
		return True
	return False

def isLiteral(w):
	if (not isNode(w)):
		print w
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
	#if ((isURI(s) or isBlankNode(s)) and isURI(p) and (isURI(o) or isBlankNode(o) or isLiteral(o))):
	#if (isNode(s) and isNode(o)):
	sTriples += 1
	#else:
	#	print ('{} {} {}'.format(s,p,o))

def checkAndAddNode(n):
	global sNodes
	global sNodesNamespace
	if(isNode(n)):# or isBlankNode(n)):
		if (not n in sNodes):
			sNodes.add(n)
	if (isNamespaceNode(n)):
		if (not n in sNodesNamespace):
			sNodesNamespace.add(n)

def countNodes(s,o):
	checkAndAddNode(s)
	checkAndAddNode(o)

def countExplicitProperties(s,p,o):
	global sProperties
	#all p
	if (not p in sProperties):
		sProperties.add(p)
	#explicit defined properties
	if (p==pMemberOfSets and o == pClass):
		if (not s in sProperties):
			sProperties.add(s)

def countSubProperties(s,p,o):
	global sProperties
	if (p==pGeneralizations and o in sProperties):
		if (not s in sProperties):
			sProperties.add(s)

def countExplicitClasses(s,p,o):
	global sClasses
	if (p == pMemberOfSets and o==oClass):
		if (not s in sClasses):
			sClasses.add(s)

def countSubClasses(s,p,o):
	if (p==pGeneralizations and o in sClasses):
		if (not s in sClasses):
			sClasses.add(s)

def countInstances(s,p,o):
	global sInstances
	global sClasses
	if (p==pGeneralizations and o in sClasses):
		if (not s in sClasses):
			if (not s in sProperties):
				if (not s in sInstances):
					sInstances.add(s)

def addIndegree(o):
	global indegreeDict
	if (isNode(o)):# or isBlankNode(o)):
		if (o in indegreeDict):
			indegreeDict[o] += 1
		else:
			indegreeDict[o] = 1

def addOutdegree(s):
	global outdegreeDict
	if (isNode(s)):# or isBlankNode(s)):
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

def readAndCountBasic(file):
	print('count triples, nodes node in-&outdegrees for {}'.format(file))
	f = open(file, 'r')
	lineCounter = 0
	for line in f:
		#print line
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		countTriple(s,p,o)
		countNodes(s,o)
		addIndegree(o)
		addOutdegree(s)
		countExplicitProperties(s,p,o)
		lineCounter += 1
		if (lineCounter % lineProgress == 0):
			print ('{} million lines read'.format(lineCounter / 1000000))
	f.close()
	print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict)))
	#print(sClasses)

def readAndCountExplicit(file):
	print('count explicit properties and classes')
	f = open(file, 'r')
	lineCounter = 0
	for line in f:
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		countExplicitProperties(s,p,o)
		countExplicitClasses(s,p,o)
	f.close()
	print('#classes: {}, properties: {}'.format(len(sClasses), len(sProperties)))

def readAndCountSub(file):
	print('count explicit properties and classes')
	f = open(file, 'r')
	lineCounter = 0
	for line in f:
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		countSubProperties(s,p,o)
		countSubClasses(s,p,o)
	f.close()
	print('#classes: {}, properties: {}'.format(len(sClasses), len(sProperties)))

def readAndCountInstances(file):
	print('count instances for {}'.format(file))
	f = open(file, 'r')
	lineCounter = 0
	for line in f:	
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		countInstances(s,p,o)
		lineCounter += 1
		if (lineCounter % lineProgress == 0):
			print ('{} million lines read'.format(lineCounter / 1000000))
	f.close()
	print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict)))

def readAndCountInstanceDegrees(file):
	print('instance in-&outdegree for {}'.format(file))
	f = open(file, 'r')
	lineCounter = 0
	for line in f:
		#print line
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		addInstanceIndegree(o)
		addInstanceOutdegree(s)
		lineCounter += 1
		if (lineCounter % lineProgress == 0):
			print ('{} million lines read'.format(lineCounter / 1000000))
	f.close()
	print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}, avgInstanceIndegree: {}, medianInstanceIndegree: {}, avgInstanceOutdegree: {}, medianInstanceOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict), getAvg(instanceIndegreeDict), getMedian(instanceIndegreeDict), getAvg(instanceOutdegreeDict), getMedian(instanceOutdegreeDict)))
try:
	print('GET STATISTICS FOR NELL')

	# get classes and properties from ontology
	readAndCountExplicit(ontology)
	readAndCountSub(ontology)
	
	#get basic KG measures
	readAndCountBasic(ontology)
	readAndCountBasic(nell)

	# get instances
	readAndCountInstances(ontology)
	readAndCountInstances(nell)
	# calculcate instance degrees
	readAndCountInstanceDegrees(ontology)
	readAndCountInstanceDegrees(nell)
	print('DONE')
	print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}, avgInstanceIndegree: {}, medianInstanceIndegree: {}, avgInstanceOutdegree: {}, medianInstanceOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict), getAvg(instanceIndegreeDict), getMedian(instanceIndegreeDict), getAvg(instanceOutdegreeDict), getMedian(instanceOutdegreeDict)))
	#print sNodes
	#print sProperties
	#print sClasses
	#print (sorted(instanceIndegreeDict.items(), key=operator.itemgetter(1)))
except:
	print('ERROR')

