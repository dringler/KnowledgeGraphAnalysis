#KG, #triples, #nodes, #properties, #classes, avgIndegree, medianIndegree, avgOutdegree, medianOutdegree
import numpy
import operator

#readFile = '/Volumes/Samsung/Wikidata/wikidata_s.nt'
readFile = 'wikidata.nt'
#readFile = '../../../SeminarPaper_KG_Files/Wikidata/wikidata_s_1m.nt'

isA = '<http://www.wikidata.org/prop/direct/P31>'

lineProgress = 30000000


sClasses = set()

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


def countClasses(p,o):
	global sClasses
	if (p == isA):
		if (not o in sClasses):
			sClasses.add(o)

try:
	print('GET STATISTICS FOR WIKIDATA')
	f = open(readFile, 'r')
	lineCounter = 0
	for line in f:
		#print line
		splittedLine = line.rstrip('\n').split()
		s, p, o = getSPO(splittedLine)
		countClasses(p,o)
		lineCounter += 1
		if (lineCounter % lineProgress == 0):
			print ('{} million lines read'.format(lineCounter / 1000000))
			#print('#triples: {}, #nodes: {}, #namespaceNodes: {}, #properties: {}, #classes: {}, #instances: {}, avgIndegree: {}, medianIndegree: {}, avgOutdegree: {}, medianOutdegree: {}'.format(sTriples, len(sNodes), len(sNodesNamespace), len(sProperties), len(sClasses), len(sInstances), getAvg(indegreeDict), getMedian(indegreeDict), getAvg(outdegreeDict), getMedian(outdegreeDict)))
	f.close()
	print('DONE')
	print('#classes: {}'.format(len(sClasses)))
except:
	print('ERROR')

