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
#top10set.update(['<http://www.wikidata.org/entity/Q5>', '<http://www.wikidata.org/entity/Q4167836>', '<http://www.wikidata.org/entity/Q16521>', '<http://www.wikidata.org/entity/Q4167410>', '<http://www.wikidata.org/entity/Q11266439>', '<http://www.wikidata.org/entity/Q13100073>', '<http://www.wikidata.org/entity/Q486972>', '<http://www.wikidata.org/entity/Q532>', '<http://www.wikidata.org/entity/Q79007>', '<http://www.wikidata.org/entity/Q13406463>'])
top10set.update(['<http://www.wikidata.org/entity/Q215627>', '<http://www.wikidata.org/entity/Q386724>'])
# instances sets
iAllSet = set() #allInstances = set()
iSet1 = set() #instancesObject = set()
iSet2 = set() #instancesObjectNoOverlap = set()
iSetAll = [iSet1, iSet2]

# indegree class instantiation 
ci1 = ClassInstances('indegree person', '<http://www.wikidata.org/entity/Q215627>')
ci2 = ClassInstances('indegree work', '<http://www.wikidata.org/entity/Q386724>')
ciAll = [ci1, ci2]

# outdegree class instantiation 
co1 = ClassInstances('outdegree person', '<http://www.wikidata.org/entity/Q215627>')
co2 = ClassInstances('outdegree work', '<http://www.wikidata.org/entity/Q386724>')
coAll = [co1, co2]

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

# count instance degrees
def countInstanceDegrees(s, o):
    if (s in iAllSet): # outdegree
        if (s in iSet1):
            co1.addInstance(s)
        elif (s in iSet2):
            co2.addInstance(s)   
    if (o in iAllSet): # indegree
        if (o in iSet1):
            ci1.addInstance(o)
        elif (o in iSet2):
            ci2.addInstance(o)
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
            print ('iSet1, #instances: {}'.format(len(iSet1)))
            print ('iSet1, #instances: {}'.format(len(iSet2)))
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
    fw = open('classInstancesDegreesForLinkedClassesResult.txt', 'w')
    saveAllResults(fw)
    fw.close()
    print ('RESULTS SAVED IN classInstancesDegreesResult.txt')
    print ('EXECUTION TIME: {} s'.format(time.time()-start))
except:
    print ('ERROR')