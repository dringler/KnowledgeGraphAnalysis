#KG, #triples, #nodes, #properties, #classes, avgIndegree, medianIndegree, avgOutdegree, medianOutdegree
import numpy
import operator


nellSample = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.esv_s.csv'
nell = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.esv.csv'
ontology = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.ontology.csv'

#rdfType = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
pClass = 'memberofsets'
#owlClass = '<http://www.w3.org/2002/07/owl#Class>'
oClass = 'concept:rtwcategory'
lineProgress = 1000000

sTriples = 0
sNodes = set()
sProperties = set()
sClasses = set()
indegreeDict = dict()
outdegreeDict = dict()



def isNode(w):
	if (w.startswith('concept') or w.startswith('http:')):
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
	if(isNode(n)):# or isBlankNode(n)):
		if (not n in sNodes):
			sNodes.add(n)

def countNodes(s,o):
	checkAndAddNode(s)
	checkAndAddNode(o)

def countProperties(p):
	global sProperties
	#if (isURI(p)):
	if (not p in sProperties):
		sProperties.add(p)

def countClasses(s,p,o):
	global sClasses
	if (p == pClass and o==oClass):
		if (not s in sClasses):
			sClasses.add(s)

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

def getAvg(d):
	return numpy.average(numpy.array(d.values()))

def getMedian(d):
	return numpy.median(numpy.array(d.values()))


def readAndCount(file):
	f = open(file, 'r')
	lineCounter = 0
	for line in f:
		#print line
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		#print o
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

try:
	print('GET STATISTICS FOR NELL')
	readAndCount(nell)
	readAndCount(ontology)
	print('DONE')
	print('#triples: {}, #nodes: {}, #properties: {}, #classes: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}'.format(sTriples, len(sNodes), len(sProperties), len(sClasses), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict)))
	#print sNodes
	#print sProperties
	#print sClasses
except:
	print('ERROR')

