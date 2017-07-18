dbpedia = 'DBpedia/owlSameAsFiles/opencyc_links.nt'
opencyc = 'OpenCyc/o2d_sameAsLinksOnly.nt'
combined = 'DO_sameAs_union.nt'

dbpediaLines = set()

try:
	f = open(dbpedia, 'r')
	for line in f:
		if (not line.startswith('#')):
			#change link from sw.cyc.com/ ... to sw.opencyc.org/
			line = line.replace('sw.cyc.com', 'sw.opencyc.org')
			dbpediaLines.add(line)
	print (len(dbpediaLines))
	#print dbpediaLines
	f.close()

	f = open(opencyc, 'r')
	for line in f:
		if line.startswith('<'):
			splittedLine = line.rstrip('n').split()
			if ('dbpedia.org' in splittedLine[2]):			
				formattedLine = splittedLine[2] + ' <http://www.w3.org/2002/07/owl#sameAs> ' + splittedLine[0] + ' .\n' 
				if (formattedLine not in dbpediaLines):
					#print formattedLine
					dbpediaLines.add(formattedLine)				
	f.close()
	print len(dbpediaLines)
	#print(dbpediaLines)

	fC = open(combined, 'w')
	for item in dbpediaLines:
		fC.write(item)

	print('DONE')
except:
	print('ERROR')