do = 'DBpedia/owlSameAsFiles/DO_sameAs_union.nt'
dw = 'DBpedia/owlSameAsFiles/DW_links.ttl'
wo = 'Wikidata/WO_over_dbpedia.nt'

def readPairs(file):
	#tupleSet = set()
	tupleDict = {}
	f = open(file, 'r')
	for line in f:
		splittedLine = line.rstrip('n').split()
		if (tupleDict.has_key(splittedLine[0])):
			tupleDict.get(splittedLine[0]).append(splittedLine[2])
		else:
			tupleDict[splittedLine[0]] = [splittedLine[2]]
		#tupleSet.add((splittedLine[0], splittedLine[2]))
	f.close()
	print '{} lines read for {}'.format(len(tupleDict), file)
	return tupleDict

try:
	doKV = readPairs(do)
	#print('{} read').format(do)
	dwKV = readPairs(dw)
	#print('{} read').format(dy)

	f = open(wo, 'w')
	dCounter = 0
	for dwK in dwKV.keys():
		#print dyK
		if (doKV.has_key(dwK)):
			#print dyK, dyV, doKV,get(dyK)
			for dwItem in dwKV.get(dwK):
				for doItem in doKV.get(dwK):
					#print dyK, dyItem, doItem
					line = ' '.join([dwItem, dwK, doItem, '.\n'])	
					f.write(line)
		dCounter += 1
		if (dCounter % 100000 == 0):
			print ('{} lines read '.format(dCounter))
	f.close()

	print('DONE')
except:
	print('ERROR')
