# class instances indegree & outdegree

import time
import numpy
import operator

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

    def addInstance(self, instance):
        if instance in self.allClassInstances:
            self.countDict[instance] += 1
        else:
            self.countDict[instance] = 1
            self.allClassInstances.add(instance)

    def calculateDegrees(self):
        # set instance degrees to zero (for the case that the degree values are 0)
        for i in instanceSetAllDict[self.uri].getSet():  # self.allClassInstances:
            if not self.countDict.has_key(i):
                self.countDict[i] = 0
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
                '<http://sw.opencyc.org/concept/Mx4rpPHhAOB1EdqAAAACs6hRXg>',
                '<http://sw.opencyc.org/concept/Mx4rvWXYgJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjE1pwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVim7ZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVji6JwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjK65wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjaApwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVinb5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVinsZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvViAkpwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVi--5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjaHZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjqs5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rv4C-JZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rAu7tPpCSQdeeTei3nO_QvQ>',
                '<http://sw.opencyc.org/concept/Mx4rvVjmoJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx8Ngh4rwP1535wpEbGdrcN5Y29ycB4rIcwFloGUQdeMlsOWYLFB2w>',
                '<http://sw.opencyc.org/concept/Mx8Ngh4rwP1535wpEbGdrcN5Y29ycB4rvViAkpwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rv3HKmpwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjZ_ZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjTtJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwQrzS5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rrPJDpCTfQdeS8IqP1a0lBw>',
                '<http://sw.opencyc.org/concept/Mx4rvVjnZ5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rv33BppwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVj1FJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjvKZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwQwcZ5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVkHM5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVj-r5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4r0EwZ9r8aEdmAAAACs6hRjg>',
                '<http://sw.opencyc.org/concept/Mx4rvVkH_ZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwClAZJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4r1-G8dDi8Ed2AAAACs6hRXg>',
                '<http://sw.opencyc.org/concept/Mx4rvViAw5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvViq35wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rFGe-SEsrEd2AAADggVfs8g>',
                '<http://sw.opencyc.org/concept/Mx4rvi9EhJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rHIBS0h_TEdaAAABQ2rksLw>',
                '<http://sw.opencyc.org/concept/Mx4rvVjntpwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVj82pwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwAXXLZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwLmi3JwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwP3teJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rv6i4pJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rv973YpwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwJaXepwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvcUMxZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwPySLpwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvViADZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvnSeAJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvViPO5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjzBJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvViIeZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVj7KJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjNlZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjReJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4r-n4BEPzcQdaYSaNf0XKNIw>',
                '<http://sw.opencyc.org/concept/Mx4rvVjQs5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjRL5wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjT95wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjUgJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVjVQJwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvViVwZwpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rvVi-55wpEbGdrcN5Y29ycA>',
                '<http://sw.opencyc.org/concept/Mx4rwQtlDpwpEbGdrcN5Y29ycA>'
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


# count all instances of the top10 classes
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
    # print ('RESULTS')
    # printClassInstanceDegreeResults()
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

    createFinalCSV()
    print ('DONE WITH PROGRAM')
except:
    print ('ERROR')