import urllib

d2w = 'DBpedia/owlSameAsFiles/wikipedia_links_en_foaf_isPrimaryTopicOf.ttl'
nell = 'NELL/NELL.08m.995.esv.csv'
combined = 'DN_over_wikipedia.nt'

w2dDict = {}
w2nDict = {}

try:
	#read d2w
	#<http://dbpedia.org/resource/River_Hamble> <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> <http://en.wikipedia.org/wiki/River_Hamble> .
	f = open(d2w, 'r')
	for line in f:
		if (not line.startswith('#')):
			splittedLine = line.rstrip('n').split()
			if (w2dDict.has_key(splittedLine[2])):
				w2dDict.get(splittedLine[2]).append(splittedLine[0])
			else:
				w2dDict[splittedLine[2]] = [splittedLine[0]]
		#if (len(w2dDict)>10000):
		#	break;
	f.close()
	print '{} lines read for d2w'.format(len(w2dDict))
	#print w2dDict

	f = open(nell, 'r')
	#concept:arthropod:silver_ground_carpet	concept:haswikipediaurl	http://en.wikipedia.org/wiki/Silver%2Dground%20Carpet	710	0.95	MBL-Iter%3A710-2013%2F03%2F13-12%3A17%3A37-From+ErrorBasedIntegrator+%28AliasMatcher%28%2C%29%29	"Silver-ground carpet" 	"http://en.wikipedia.org/wiki/Silver%2Dground%20Carpet" 	Silver-ground carpet	http://en.wikipedia.org/wiki/Silver%2Dground%20Carpet			%5BAliasMatcher-Iter%3A621-2012%2F08%2F03-10%3A35%3A59-%3Ctoken%3D%3E-Freebase+7%2F9%2F2012%5D
	for line in f:
		if ('concept:haswikipediaurl' in line):
			splittedLine = line.rstrip('n').split()
			splittedLine[2] = '<' + splittedLine[2] + ">"
			splittedLine[2] = splittedLine[2].replace('%20','_')
			splittedLine[2] = urllib.unquote(splittedLine[2])
			#splittedLine[2] = splittedLine[2].replace('%2D','-')
			if (w2nDict.has_key(splittedLine[2])):
				w2nDict.get(splittedLine[2]).append(splittedLine[0])
			else:
				w2nDict[splittedLine[2]] = [splittedLine[0]]
		#if (len(w2nDict)>10000):
		#	break;

	f.close()
	print '{} lines read for n2w'.format(len(w2nDict))
	#print w2nDict

	nKeyFile = open('Nell_all_wikipedia_keys.txt', 'w')
	for key in w2nDict.keys():
		nKeyFile.write(key+'\n')
	nKeyFile.close()
	print('all keys in NELL written to Nell_wikipedia.txt')

	fC = open(combined, 'w')
	dnCounter = 0
	matched = 0
	for w2dK in w2dDict.keys():
		#print w2dK
		if (w2nDict.has_key(w2dK)):
			#print w2dK, w2dDict,get(w2dK), w2nDict,get(w2dK)
			for wdItem in w2dDict.get(w2dK):
				for wnItem in w2nDict.get(w2dK):
					#print dyK, dyItem, doItem
					line = ' '.join([wdItem, w2dK, wnItem, '.\n'])	
					#print line
					fC.write(line)
					matched += 1;
		dnCounter += 1
		if (dnCounter % 2000000 == 0):
			print ('{} lines read '.format(dnCounter))
	fC.close()
	print ('{} matches found'.format(matched))
	print('DONE')
except:
	print('ERROR')




	
	