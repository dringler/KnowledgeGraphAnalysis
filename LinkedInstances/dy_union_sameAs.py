dbpedia = 'DBpedia/owlSameAsFiles/yago_links.nt'
yago = 'YAGO/yagoDBpediaInstances.ttl'
combined = 'DY_sameAs_union.nt'

dbpediaLines = set()

try:
	fD = open(dbpedia, 'r')
	for line in fD:
		if (not line.startswith('#')):
			dbpediaLines.add(line)
	print (len(dbpediaLines))
	#print dbpediaLines
	fD.close()

	fY = open(yago, 'r')
	for line in fY:
		if line.startswith('<'):
			splittedLine = line.rstrip('n').split()
			if ('dbpedia.org' in splittedLine[2]):
				#<http://yago-knowledge.org/resource/INSTANCENAME> .
				splittedLine[0] = '<http://yago-knowledge.org/resource/' + splittedLine[0][1:]
				#print splittedLine[0]
				formattedLine = splittedLine[2] + ' <http://www.w3.org/2002/07/owl#sameAs> ' + splittedLine[0] + ' .\n' 
				if (formattedLine not in dbpediaLines):
					#print formattedLine
					dbpediaLines.add(formattedLine)				
	fY.close()
	print len(dbpediaLines)
	#print(dbpediaLines)

	fC = open(combined, 'w')
	for item in dbpediaLines:
		fC.write(item)
	fC.close()
	print('DONE')
except:
	print('ERROR')