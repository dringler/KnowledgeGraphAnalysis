dn = 'DBpedia/owlSameAsFiles/DN_over_wikipedia.nt'
dy = 'DBpedia/owlSameAsFiles/DY_sameAs_union.nt'
yn =  'YAGO/YN_over_dbpedia.nt'

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
	dnKV = readPairs(dn)
	#print('{} read').format(do)
	dyKV = readPairs(dy)
	#print('{} read').format(dy)

	f = open(yn, 'w')
	dnCounter = 0
	for dyK in dyKV.keys():
		#print dyK
		if (dnKV.has_key(dyK)):
			#print dyK, dyV, doKV,get(dyK)
			for dyItem in dyKV.get(dyK):
				for dnItem in dnKV.get(dyK):
					#print dyK, dyItem, doItem
					line = ' '.join([dyItem, dyK, dnItem, '.\n'])	
					f.write(line)
		dnCounter += 1
		if (dnCounter % 100000 == 0):
			print ('{} lines read '.format(dnCounter))
	f.close()

	print('DONE')
except:
	print('ERROR')
