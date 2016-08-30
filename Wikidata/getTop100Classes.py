# get top 10 classes

import os
import time
import string
import itertools
import re
import operator

start = time.time()

#readFile = 'Wikidata/wikidata_sample_1m.nt'
#readFile = '../../../../Volumes/Public/SeminarPaper/wikidata-20160801-all-BETA.ttl'
#readFile = '/Volumes/Samsung/wikidata_sample_from_nt.nt'
readFile = '/Volumes/Samsung/wikidata.nt'

classCount = {} #key:class, value:count

lineProgress = 10000000

# ADAPT TO WIKIDATA
rdfType = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
owlClass = '<http://www.w3.org/2002/07/owl#Class>'
isA = '<http://www.wikidata.org/prop/direct/P31>'

# <http://www.wikidata.org/prop/novalue/P16> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> .


allClasses = set()
seenClasses = set()
classCountDic = {} #key:class, value:count

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
        
def countClasses(o):
    #print ('countClasses')
    if o in seenClasses:
        classCountDic[o] += 1
    else:
        classCountDic[o] = 1
        seenClasses.add(o)
        
print('START')
try:
    f = open(readFile, 'r')
    lineCounter = 0
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
        #if (p == rdfType and o == owlClass):
        if (p == isA):
            #print ('{} {} {}'.format(s,p,o))
            countClasses(o)
        lineCounter += 1
        if (lineCounter % lineProgress == 0):
            print ('{} lines read'.format(lineCounter))
        #if (lineCounter > 100):
        #    break
    top100dic = dict(sorted(classCountDic.items(), key=operator.itemgetter(1), reverse=True)[:100])
    print (sorted(top100dic.items(), key=operator.itemgetter(1), reverse=True))
    f.close()
    print ('DONE')
except:
    print('ERROR')
print ('EXECUTION TIME: {} s'.format(time.time()-start))