do = 'DBpedia/owlSameAsFiles/DO_sameAs_union.nt'
dy = 'DBpedia/owlSameAsFiles/DY_sameAs_union.nt'
yo = 'YAGO/YO_over_dbpedia.nt'

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
	dyKV = readPairs(dy)
	#print('{} read').format(dy)

	f = open(yo, 'w')
	dyCounter = 0
	for dyK in dyKV.keys():
		#print dyK
		if (doKV.has_key(dyK)):
			#print dyK, dyV, doKV,get(dyK)
			for dyItem in dyKV.get(dyK):
				for doItem in doKV.get(dyK):
					#print dyK, dyItem, doItem
					line = ' '.join([dyItem, dyK, doItem, '.\n'])	
					f.write(line)
		dyCounter += 1
		if (dyCounter % 100000 == 0):
			print ('{} lines read '.format(dyCounter))
	f.close()

	print('DONE')
except:
	print('ERROR')
