# get owl:sameAs links

import os
import time
import string
import itertools
import re
import operator

start = time.time()

#readFile = 'OpenCyc/opencyc-latest_sample.nt'
readFile = '../../../SeminarPaper_KG_files/OpenCyc/opencyc-latest.nt'

kgCountDict = {} #key:class, value:count

lineProgress = 1000000

owlSameAs = '<http://www.w3.org/2002/07/owl#sameAs>'
kgNames = ["dbpedia", "yago", "nell", "wikidata", "wordnet", "synset"]
for kgName in kgNames:
    kgCountDict[kgName] = 0

yagoSet = set()
nellSet = set()
wordnetSet = set()
synsetSet = set()

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

def addToCount(o):
    for kgName in kgNames:
        if kgName in o.lower():
            kgCountDict[kgName] +=1
            if "yago" in kgName:
                yagoSet.add(o)
            if "nell" in kgName:
                nellSet.add(o)
            if "wordnet" in kgName:
                wordnetSet.add(o)
            if "synset" in kgName:
                synsetSet.add(o)
        
def countClasses(o):
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
        if (p == owlSameAs and any(kgName in o.lower() for kgName in kgNames)):
            #print line
            addToCount(o)
        #if "wordnet" in line.lower():
        #    print line
        lineCounter += 1
        if (lineCounter % lineProgress == 0):
            print ('{} lines read'.format(lineCounter))
        #if (lineCounter > 100):
        #    break
    f.close()
    for k,v in kgCountDict.iteritems():
        print ('{}: {} owl:sameAs links'.format(k, v))
    #print (yagoSet)
    #print (nellSet)
    #for i in synsetSet:
    #    print i
    print ('DONE')
except:
    print('ERROR')
print ('EXECUTION TIME: {} s'.format(time.time()-start))