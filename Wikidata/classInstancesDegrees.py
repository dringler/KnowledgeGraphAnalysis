# class instances indegree & outdegree

import os
import time
import string
import itertools
import re
import operator
import numpy

start = time.time()

#readFile = '/Volumes/Samsung/wikidata_sample_from_nt.nt'
readFile = '/Volumes/Samsung/wikidata.nt'

isA = '<http://www.wikidata.org/prop/direct/P31>'

lineProgress = 10000000

class ClassInstances:
    def __init__(self, name, uri):
        self.name = name
        self.uri = uri
        self.allClassInstances = set()
        self.countDict = dict()
        self.min = 9999999
        self.max = 0
        self.avg = 0.0
        self.median = 0.0
    def getURI(self):
        return self.uri
    def getClassInstances(self):
        return len(self.allClassInstances)
    def addInstance(self, instance):
        if instance in self.allClassInstances:
            self.countDict[instance] += 1
        else:
            self.countDict[instance] = 1
            self.allClassInstances.add(instance)
    def calculateDegrees(self):
        allValueList = []
        for k,v in self.countDict.iteritems():
            allValueList.append(v)
            if (v < self.min):
                self.min = v
            if (v > self.max):
                self.max = v
            self.avg += v
        if (len(self.countDict) != 0):
            self.avg = self.avg / len(self.countDict)
        if (len(allValueList) != 0):
            self.median = numpy.median(numpy.array(allValueList))
    def printResults(self):
        print ('{}'.format(self.name))
        print ('min: {}, avg: {}, median: {}, max: {}'.format(self.min, self.avg, self.median, self.max))
    def saveResults(self, wFile):
        wFile.write(self.name + '\n' + self.uri + '\n')
        wFile.write('min: {}\n'.format(self.min))
        wFile.write('avg: {}\n'.format(self.avg))
        wFile.write('median: {}\n'.format(self.median))
        wFile.write('max: {}\n'.format(self.max))
        wFile.write('\n')

# top 10 classes     
top10set = set()
top10set.update(['<http://www.wikidata.org/entity/Q5>', '<http://www.wikidata.org/entity/Q4167836>', '<http://www.wikidata.org/entity/Q16521>', '<http://www.wikidata.org/entity/Q4167410>', '<http://www.wikidata.org/entity/Q11266439>', '<http://www.wikidata.org/entity/Q13100073>', '<http://www.wikidata.org/entity/Q486972>', '<http://www.wikidata.org/entity/Q532>', '<http://www.wikidata.org/entity/Q79007>', '<http://www.wikidata.org/entity/Q13406463>'])

# instances sets
iAllSet = set() #allInstances = set()
iSet1 = set() #instancesObject = set()
iSet2 = set() #instancesObjectNoOverlap = set()
iSet3 = set() #instancesNoun = set()
iSet4 = set() #instancesOrganism = set()
iSet5 = set() #instancesIndividual = set()
iSet6 = set() #instancesProgram = set()
iSet7 = set() #instancesCollection = set()
iSet8 = set() #instancesCity = set()
iSet9 = set() #instancesTempObject = set()
iSet10 = set() #instancesCoorp = set()
iSetAll = [iSet1, iSet2, iSet3, iSet4, iSet5, iSet6, iSet7, iSet8, iSet9, iSet10]

# indegree class instantiation 
ci1 = ClassInstances('indegree human Q5', '<http://www.wikidata.org/entity/Q5>')
ci2 = ClassInstances('indegree wikimedia category', '<http://www.wikidata.org/entity/Q4167836>')
ci3 = ClassInstances('indegree taxon Q16521', '<http://www.wikidata.org/entity/Q16521>')
ci4 = ClassInstances('indegree wikimedia disambiguation page Q4167410', '<http://www.wikidata.org/entity/Q4167410>')
ci5 = ClassInstances('indegree wikimedia template Q11266439', '<http://www.wikidata.org/entity/Q11266439>')
ci6 = ClassInstances('indegree village-level division in china Q13100073', '<http://www.wikidata.org/entity/Q13100073>')
ci7 = ClassInstances('indegree human settlement Q486972', '<http://www.wikidata.org/entity/Q486972>')
ci8 = ClassInstances('indegree village Q532', '<http://www.wikidata.org/entity/Q532>')
ci9 = ClassInstances('indegree street Q79007', '<http://www.wikidata.org/entity/Q79007>')
ci10 = ClassInstances('indegree wikimedia list article Q13406463', '<http://www.wikidata.org/entity/Q13406463>')
ciAll = [ci1, ci2, ci3, ci4, ci5, ci6, ci7, ci8, ci9, ci10]

# outdegree class instantiation 
co1 = ClassInstances('outdegree human Q5', '<http://www.wikidata.org/entity/Q5>')
co2 = ClassInstances('outdegree wikimedia category', '<http://www.wikidata.org/entity/Q4167836>')
co3 = ClassInstances('outdegree taxon Q16521', '<http://www.wikidata.org/entity/Q16521>')
co4 = ClassInstances('outdegree wikimedia disambiguation page Q4167410', '<http://www.wikidata.org/entity/Q4167410>')
co5 = ClassInstances('outdegree wikimedia template Q11266439', '<http://www.wikidata.org/entity/Q11266439>')
co6 = ClassInstances('outdegree village-level division in china Q13100073', '<http://www.wikidata.org/entity/Q13100073>')
co7 = ClassInstances('outdegree human settlement Q486972', '<http://www.wikidata.org/entity/Q486972>')
co8 = ClassInstances('outdegree village Q532', '<http://www.wikidata.org/entity/Q532>')
co9 = ClassInstances('outdegree street Q79007', '<http://www.wikidata.org/entity/Q79007>')
co10 = ClassInstances('outdegree wikimedia list article Q13406463', '<http://www.wikidata.org/entity/Q13406463>')
#co10 = ClassInstances('outdegree public coorporation', '<http://sw.opencyc.org/concept/Mx4rvVjZ_ZwpEbGdrcN5Y29ycA>')
coAll = [co1, co2, co3, co4, co5, co6, co7, co8, co9, co10]

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

# count all instances of the top10 classes
def countInstances(s, o):
    #s a o: s=instance, o=class 
    iAllSet.add(s)
    if (o == ci1.getURI()):
        if (s not in iSet1):
            iSet1.add(s)
        return
    if (o == ci2.getURI()):
        if (s not in iSet2):
            iSet2.add(s)
        return
    if (o == ci3.getURI()):
        if (s not in iSet3):
            iSet3.add(s)
        return
    if (o == ci4.getURI()):
        if (s not in iSet4):
            iSet4.add(s)
        return
    if (o == ci5.getURI()):
        if (s not in iSet5):
            iSet5.add(s)
        return
    if (o == ci6.getURI()):
        if (s not in iSet6):
            iSet6.add(s)
        return
    if (o == ci7.getURI()):
        if (s not in iSet7):
            iSet7.add(s)
        return
    if (o == ci8.getURI()):
        if (s not in iSet8):
            iSet8.add(s)
        return
    if (o == ci9.getURI()):
        if (s not in iSet9):
            iSet9.add(s)
        return
    if (o == ci10.getURI()):
        if (s not in iSet10):
            iSet10.add(s)
        return

# count instance degrees
def countInstanceDegrees(s, o):
    if (s in iAllSet): # outdegree
        if (s in iSet1):
            co1.addInstance(s)
        elif (s in iSet2):
            co2.addInstance(s)
        elif (s in iSet3):
            co3.addInstance(s)
        elif (s in iSet4):
            co4.addInstance(s)
        elif (s in iSet5):
            co5.addInstance(s)
        elif (s in iSet6):
            co6.addInstance(s)
        elif (s in iSet7):
            co7.addInstance(s)
        elif (s in iSet8):
            co8.addInstance(s)
        elif (s in iSet9):
            co9.addInstance(s)
        elif (s in iSet10):
            co10.addInstance(s)       
    if (o in iAllSet): # indegree
        if (o in iSet1):
            ci1.addInstance(o)
        elif (o in iSet2):
            ci2.addInstance(o)
        elif (o in iSet3):
            ci3.addInstance(o)
        elif (o in iSet4):
            ci4.addInstance(o)
        elif (o in iSet5):
            ci5.addInstance(o)
        elif (o in iSet6):
            ci6.addInstance(o)
        elif (o in iSet7):
            ci7.addInstance(o)
        elif (o in iSet8):
            ci8.addInstance(o)
        elif (o in iSet9):
            ci9.addInstance(o)
        elif (o in iSet10):
            ci10.addInstance(o)
        return

def calculateClassDegrees():
    for item in ciAll:
        item.calculateDegrees()
    for item in coAll:
        item.calculateDegrees()
        
def printClassDegreeResults():
    for item in ciAll:
        item.printResults()
    for item in coAll:
        item.printResults()

def saveAllResults(fw):
    for item in ciAll:
        item.saveResults(fw)
    for item in coAll:
        item.saveResults(fw)
        
print('START')
try:
    # get all instances for the top10 classes
    f = open(readFile, 'r')
    lineCounter = 0
    print ('START COUNTING INSTANCES')
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
        if (p == isA and o in top10set):
            countInstances(s, o)
        lineCounter += 1
        if (lineCounter % lineProgress == 0):
            print ('{} million lines read'.format(lineCounter/1000000))
        #if (lineCounter > 5000):
        #    break
    f.close()
    print ('DONE COUNTING INSTANCES')
    for i, item in enumerate(iSetAll):
        print ('iSet{}, #instances: {}'.format(i+1, len(iSetAll[i])))
        
    # count instance degrees
    print ('START COUNTING INSTANCE DEGREES')
    f = open(readFile, 'r')
    lineCounter = 0
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
        countInstanceDegrees(s, o)
        lineCounter += 1
        if (lineCounter % lineProgress == 0):
            print ('{} million lines read'.format(lineCounter/1000000))
        #if (lineCounter > 5000):
        #    break
    f.close()
    print ('DONE COUNTING INSTANCE DEGREES')
    print ('START CALCULATING CLASS DEGREES')
    calculateClassDegrees()
    print ('DONE CALCULATING CLASS DEGREES')
    print ('RESULTS')
    printClassDegreeResults()
    fw = open('classInstancesDegreesResult.txt', 'w')
    saveAllResults(fw)
    fw.close()
    print ('RESULTS SAVED IN classInstancesDegreesResult.txt')
    print ('EXECUTION TIME: {} s'.format(time.time()-start))
except:
    print ('ERROR')