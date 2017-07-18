dn = 'DBpedia/owlSameAsFiles/DN_over_wikipedia.nt'
do = 'DBpedia/owlSameAsFiles/DO_sameAs_union.nt'
on = 'OpenCyc/ON_over_dbpedia.nt'

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
	doKV = readPairs(do)
	#print('{} read').format(dy)

	f = open(on, 'w')
	dCounter = 0
	for doK in doKV.keys():
		#print dyK
		if (dnKV.has_key(doK)):
			#print dyK, dyV, doKV,get(dyK)
			for doItem in doKV.get(doK):
				for dnItem in dnKV.get(doK):
					#print dyK, dyItem, doItem
					line = ' '.join([doItem, doK, dnItem, '.\n'])	
					f.write(line)
		dCounter += 1
		if (dCounter % 1000000 == 0):
			print ('{} lines read '.format(dCounter))
	f.close()

	print('DONE')
except:
	print('ERROR')
