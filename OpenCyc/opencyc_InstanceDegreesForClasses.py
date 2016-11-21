# class instances indegree & outdegree

import time
import numpy
import operator
from collections import *

#readFile = 'opencyc-latest_sample.nt'
readFile = '../../../SeminarPaper_KG_Files/opencyc-latest.nt'


rdfType = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
owlClass = '<http://www.w3.org/2002/07/owl#Class>'

lineProgress = 1000000


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

    def getName(self):
        return self.name

    def getURI(self):
        return self.uri

    def getClassInstances(self):
        return len(self.allClassInstances)

    def getMin(self):
        return self.min

    def getAvg(self):
        return self.avg

    def getMed(self):
        return self.median

    def getMax(self):
        return self.max

    def getCountDict(self):
        return self.countDict

    def addInstance(self, instance):
        if instance in self.allClassInstances:
            self.countDict[instance] += 1
        else:
            self.countDict[instance] = 1
            self.allClassInstances.add(instance)

    def calculateDegrees(self):
        print ('len(self.countDict): {}',format(len(self.countDict)))
        # init instance degrees
        print('INITIALIZE INSTANCE DEGREES')
        # set instance degrees to zero (for the case that the degree values are 0)
        for i in instanceSetAllDict[self.uri].getSet():#self.allClassInstances:
            if not self.countDict.has_key(i):
                self.countDict[i] = 0
        print ('len(self.countDict): {}', format(len(self.countDict)))
        allValueList = []
        for k, v in self.countDict.iteritems():
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
        print ('{} for {}'.format(self.name, self.uri))
        print ('min: {}, avg: {}, median: {}, max: {}'.format(self.min, self.avg, self.median, self.max))

    def saveResults(self, wFile):
        wFile.write(self.name + '\n' + self.uri + '\n')
        wFile.write('min: {}\n'.format(self.min))
        wFile.write('avg: {}\n'.format(self.avg))
        wFile.write('median: {}\n'.format(self.median))
        wFile.write('max: {}\n'.format(self.max))
        wFile.write('\n')


class InstanceSet:
    def __init__(self, ):
        self.iSet = set()
    def getSet(self):
        return self.iSet


# top classes
topClassSet = set()
# instances sets
allInstances = set()  # allInstances = set()
topClassSet.update([
                '<http://sw.opencyc.org/concept/Mx4rrPJDpCTfQdeS8IqP1a0lBw>', # populated place (indegree)
                '<http://sw.opencyc.org/concept/Mx4rvVinb5wpEbGdrcN5Y29ycA>', # intelligent agent (indegree)
                '<http://sw.opencyc.org/concept/Mx4rvViAkpwpEbGdrcN5Y29ycA>', # person (indegree)
                '<http://sw.opencyc.org/concept/Mx4rwLmi3JwpEbGdrcN5Y29ycA>' # album-cw (indegree)
            ])

indegreeDic = {}  # key:class, value: indegree
outdegreeDic = {}  # key: class, value: outdegree

instanceSetAllDict = {}
ciAll = {}
coAll = {}
for uri in topClassSet:
    instanceSetAllDict[uri] = InstanceSet()
    ciAll[uri] = ClassInstances('indegree', uri)
    coAll[uri] = ClassInstances('outdegree', uri)


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


# count all instances of the top classes
def countInstances(s, o):
    # s a o: s=instance, o=class
    allInstances.add(s)
    if o in instanceSetAllDict.keys():
        i = instanceSetAllDict[o]
        iSet = i.getSet()
        if (s not in iSet):
            iSet.add(s)


# count instance degrees
def countInstanceDegrees(s, o):
    if (s in allInstances):  # outdegree
        for uri, i in instanceSetAllDict.items():
            if s in i.getSet():
                coSet = coAll[uri]
                coSet.addInstance(s)
    if (o in allInstances):  # indegree
        for uri, i in instanceSetAllDict.items():
            if o in i.getSet():
                ciSet = ciAll[uri]
                ciSet.addInstance(o)

def getTopXIndegreeInstances(x):
    for uri, i in ciAll.items():
        ciCountDict = ciAll[uri].getCountDict()
        sortedCiCountDict = OrderedDict(sorted(ciCountDict.items(), key=lambda t: t[1], reverse=True))

        print('INSTANCES In instanceSetAllDict: {}'.format(len(instanceSetAllDict[uri].getSet())))

        print ('TOP INSTANCES FOR CLASS: {} with {} instances having a degree of at least 1'.format(uri, ciAll[uri].getClassInstances()))
        print ('MEDIAN: {}'.format(ciAll[uri].getMed()))
        counter = 0
        for k, v in sortedCiCountDict.items():
            if counter>x:
                break
            print k,v
            counter += 1



def calculateClassInstanceDegrees():
    for k, v in ciAll.items():
        v.calculateDegrees()
    for k, v in coAll.items():
        v.calculateDegrees()


def printClassInstanceDegreeResults():
    for k, v in ciAll.items():
        v.printResults()
    for k, v in coAll.items():
        v.printResults()


def saveAllResults(fw):
    for k, v in ciAll.items():
        v.saveResults(fw)
    for k, v in coAll.items():
        v.saveResults(fw)


def classDegrees(file):
    start = time.time()
    global indegreeDic
    global outdegreeDic
    # get all instances & the indegree and outdegree for the top classes
    f = open(file, 'r')
    lineCounter = 0
    print ('START COUNTING INSTANCES AND CALCULATING CLASS DEGREES')
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
        if (p == rdfType and o in topClassSet):
            countInstances(s, o)
        if s in topClassSet:
            if outdegreeDic.has_key(s):
                outdegreeDic[s] += 1
            else:
                outdegreeDic[s] = 1
        if o in topClassSet:
            if indegreeDic.has_key(o):
                indegreeDic[o] += 1
            else:
                indegreeDic[o] = 1
        lineCounter += 1
        if (lineCounter % lineProgress == 0):
            print ('{} million lines read'.format(lineCounter / 1000000))
            # if (lineCounter > 5000):
            #    break
    f.close()
    print ('DONE COUNTING INSTANCES')
    # for k, v in instanceSetAllDict.items():
    #    print ('set for {}, #instances: {}'.format(k, len(v.getSet())))

    print ('DONE CALCULATING CLASS DEGREES')
    # print ('INDEGREE RESULT:')
    # print sorted(indegreeDic.items(), key=operator.itemgetter(1), reverse=True)
    # print ('OUTDEGREE RESULT')
    # print sorted(outdegreeDic.items(), key=operator.itemgetter(1), reverse=True)

    print ("Write results to classDegreeResult.txt")
    fw = open('classDegreesResult.txt', 'w')
    fw.write("class indegree results\n")
    for k, v in indegreeDic.items():
        fw.write('{}: {}\n'.format(k, v))
    fw.write("\nclass outdegree results\n")
    for k, v in outdegreeDic.items():
        fw.write('{}: {}\n'.format(k, v))
    print ("Results written to classDegreeResult.txt")

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
            print ('{} million lines read'.format(lineCounter / 1000000))
            # if (lineCounter > 5000):
            #    break
    f.close()
    print ('DONE COUNTING INSTANCE DEGREES')
    print ('START CALCULATING CLASS DEGREES')
    calculateClassInstanceDegrees()
    print ('DONE CALCULATING CLASS DEGREES')

    # get top 10 instances for each class
    print ('GET TOP 10 INSTANCES REGARING INDEGREE FOR EACH CLASS')
    getTopXIndegreeInstances(10)

    print ('RESULTS')
    printClassInstanceDegreeResults()
    fw = open('classInstancesDegreesResult.txt', 'w')
    saveAllResults(fw)
    fw.close()
    print ('RESULTS SAVED IN classInstancesDegreesResult.txt')
    print ('EXECUTION TIME: {} s'.format(time.time() - start))


def createFinalCSV():
    print ('WRITE FINAL CSV')
    fw = open('allDegrees.csv', 'w')
    fw.write(
        'class, instance count, class indegree, class outdegree, classInstanceIndegreeMin,	classInstanceIndegreeAvg, classInstanceIndegreeMedian, classInstanceIndegreeMax, classInstanceOutdegreeMin,	classInstanceOutdegreeAvg, classInstanceOutdegreeMedian, classInstanceOutdegreeMax\n')
    for uri in topClassSet:
        instanceCount = 0
        indegreeResult = 0
        outdegreeResult = 0
        if uri in instanceSetAllDict:
            instanceCount = len(instanceSetAllDict[uri].getSet())
        if uri in indegreeDic:
            indegreeResult = indegreeDic[uri]
        if uri in outdegreeDic:
            outdegreeResult = outdegreeDic[uri]
        ciMin = ciAll[uri].getMin()
        if ciMin == 9999999:
            ciMin = 0
        coMin = coAll[uri].getMin()
        if coMin == 9999999:
            coMin = 0

        fw.write('{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}\n'.format(
            uri,
            instanceCount,
            indegreeResult,
            outdegreeResult,
            ciMin,
            ciAll[uri].getAvg(),
            ciAll[uri].getMed(),
            ciAll[uri].getMax(),
            coMin,
            coAll[uri].getAvg(),
            coAll[uri].getMed(),
            coAll[uri].getMax()
        ))
    fw.close()
    print ('DONE WRITING FINAL CSV TO allDegrees.csv')


try:
    print('START')
    classDegrees(readFile)

    #createFinalCSV()
    print ('DONE WITH PROGRAM')
except:
    print ('ERROR')