dw = 'DBpedia/owlSameAsFiles/DW_links.ttl'
dy = 'DBpedia/owlSameAsFiles/DY_sameAs_union.nt'
yw =  'YAGO/YW_over_dbpedia.nt'

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
	dwKV = readPairs(dw)
	#print('{} read').format(do)
	dyKV = readPairs(dy)
	#print('{} read').format(dy)

	f = open(yw, 'w')
	dwCounter = 0
	for dyK in dyKV.keys():
		#print dyK
		if (dwKV.has_key(dyK)):
			#print dyK, dyV, doKV,get(dyK)
			for dyItem in dyKV.get(dyK):
				for dwItem in dwKV.get(dyK):
					#print dyK, dyItem, doItem
					line = ' '.join([dyItem, dyK, dwItem, '.\n'])	
					f.write(line)
		dwCounter += 1
		if (dwCounter % 100000 == 0):
			print ('{} lines read '.format(dwCounter))
	f.close()

	print('DONE')
except:
	print('ERROR')
