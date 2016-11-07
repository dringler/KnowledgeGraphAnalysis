# class instances indegree & outdegree

import time
import numpy
import operator



#readFile = '/Volumes/Samsung/wikidata_sample_from_nt.nt'
readFile = '/Volumes/Samsung/Wikidata/wikidata.nt'
#readFile = 'wikidata_sample_1m.nt'

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
    def __init__(self,):
        self.iSet = set()
    def getSet(self):
        return self.iSet



# top 10 classes     
topClassSet = set()
# instances sets
allInstances = set() #allInstances = set()
topClassSet.update([#'<http://www.wikidata.org/entity/Q5>', '<http://www.wikidata.org/entity/Q4167836>', '<http://www.wikidata.org/entity/Q16521>', '<http://www.wikidata.org/entity/Q4167410>', '<http://www.wikidata.org/entity/Q11266439>', '<http://www.wikidata.org/entity/Q13100073>', '<http://www.wikidata.org/entity/Q486972>', '<http://www.wikidata.org/entity/Q532>', '<http://www.wikidata.org/entity/Q79007>', '<http://www.wikidata.org/entity/Q13406463>'])
                        '<http://www.wikidata.org/entity/Q223557>',
                        '<http://www.wikidata.org/entity/Q337060>',
                        '<http://www.wikidata.org/entity/Q7239>',
                        '<http://www.wikidata.org/entity/Q16521>',
                        '<http://www.wikidata.org/entity/Q7432>',
                        '<http://www.wikidata.org/entity/Q795052>',
                        '<http://www.wikidata.org/entity/Q24229398>',
                        '<http://www.wikidata.org/entity/Q5>',
                        '<http://www.wikidata.org/entity/Q215627>',
                        '<http://www.wikidata.org/entity/Q2066131>',
                        '<http://www.wikidata.org/entity/Q33999>',
                        '<http://www.wikidata.org/entity/Q2526255>',
                        '<http://www.wikidata.org/entity/Q211236>',
                        '<http://www.wikidata.org/entity/Q6581097>',
                        '<http://www.wikidata.org/entity/Q17519152>',
                        '<http://www.wikidata.org/entity/Q16334298>',
                        '<http://www.wikidata.org/entity/Q16334295>',
                        '<http://www.wikidata.org/entity/Q327333>',
                        '<http://www.wikidata.org/entity/Q891723>',
                        '<http://www.wikidata.org/entity/Q2221906>',
                        '<http://www.wikidata.org/entity/Q1970725>',
                        '<http://www.wikidata.org/entity/Q486972>',
                        '<http://www.wikidata.org/entity/Q515>',
                        '<http://www.wikidata.org/entity/Q532>',
                        '<http://www.wikidata.org/entity/Q3957>',
                        '<http://www.wikidata.org/entity/Q79007>',
                        '<http://www.wikidata.org/entity/Q40080>',
                        '<http://www.wikidata.org/entity/Q130003>',
                        '<http://www.wikidata.org/entity/Q4022>',
                        '<http://www.wikidata.org/entity/Q1248784>',
                        '<http://www.wikidata.org/entity/Q673661>',
                        '<http://www.wikidata.org/entity/Q8205328>',
                        '<http://www.wikidata.org/entity/Q386724>',
                        '<http://www.wikidata.org/entity/Q178659>',
                        '<http://www.wikidata.org/entity/Q11471>',
                        '<http://www.wikidata.org/entity/Q26907166>',
                        '<http://www.wikidata.org/entity/Q1084>',
                        '<http://www.wikidata.org/entity/Q11266439>',
                        '<http://www.wikidata.org/entity/Q4167410>',
                        '<http://www.wikidata.org/entity/Q4167836>',
                        '<http://www.wikidata.org/entity/Q13406463>',
                        '<http://www.wikidata.org/entity/Q82955>',
                        '<http://www.wikidata.org/entity/Q7278>',
                        '<http://www.wikidata.org/entity/Q2188189>',
                        '<http://www.wikidata.org/entity/Q482994>',
                        '<http://www.wikidata.org/entity/Q7302866>',
                        '<http://www.wikidata.org/entity/Q134556>',
                        '<http://www.wikidata.org/entity/Q11424>',
                        '<http://www.wikidata.org/entity/Q571>',
                        '<http://www.wikidata.org/entity/Q7397>',
                        '<http://www.wikidata.org/entity/Q349>',
                        '<http://www.wikidata.org/entity/Q1656682>',
                        '<http://www.wikidata.org/entity/Q350604>',
                        '<http://www.wikidata.org/entity/Q198>',
                        '<http://www.wikidata.org/entity/Q3010205>',
                        '<http://www.wikidata.org/entity/Q16510064>',
                        '<http://www.wikidata.org/entity/Q6256>',
                        '<http://www.wikidata.org/entity/Q41176>',
                        '<http://www.wikidata.org/entity/Q79529>',
                        '<http://www.wikidata.org/entity/Q11344>',
                        '<http://www.wikidata.org/entity/Q6999>',
                        '<http://www.wikidata.org/entity/Q634>',
                        '<http://www.wikidata.org/entity/Q42889>',
                        '<http://www.wikidata.org/entity/Q334166>',
                        '<http://www.wikidata.org/entity/Q1420>',
                        '<http://www.wikidata.org/entity/Q11446>',
                        '<http://www.wikidata.org/entity/Q40218>'
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
    #s a o: s=instance, o=class 
    allInstances.add(s)
    if o in instanceSetAllDict.keys():
        i = instanceSetAllDict[o]
        iSet = i.getSet()
        if (s not in iSet):
            iSet.add(s)

# count instance degrees
def countInstanceDegrees(s, o):
    if (s in allInstances): # outdegree
        for uri,i in instanceSetAllDict.items():
            if s in i.getSet():
                coSet = coAll[uri]
                coSet.addInstance(s)
    if (o in allInstances):  # indegree
        for uri,i in instanceSetAllDict.items():
            if o in i.getSet():
                ciSet = ciAll[uri]
                ciSet.addInstance(o)


def calculateClassInstanceDegrees():
    for k,v in ciAll.items():
        v.calculateDegrees()
    for k,v in coAll.items():
        v.calculateDegrees()
        
def printClassInstanceDegreeResults():
    for k,v in ciAll.items():
        v.printResults()
    for k,v in coAll.items():
        v.printResults()

def saveAllResults(fw):
    for k,v in ciAll.items():
        v.saveResults(fw)
    for k,v in coAll.items():
        v.saveResults(fw)

def classDegrees(file):
    start = time.time()
    global indegreeDic
    global outdegreeDic
    print ('START CALCULATING CLASS DEGREES')
    f = open(file, 'r')
    lineCounter = 0
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
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
    f.close()
    print ('DONE CALCULATING CLASS DEGREES')
    print ('INDEGREE RESULT:')
    print sorted(indegreeDic.items(), key=operator.itemgetter(1), reverse=True)
    print ('OUTDEGREE RESULT')
    print sorted(outdegreeDic.items(), key=operator.itemgetter(1), reverse=True)

    print ("Write results to classDegreeResult.txt")
    fw = open('classDegreesResult.txt', 'w')
    fw.write("class indegree results\n")
    for k,v in indegreeDic.items():
        fw.write('{}: {}\n'.format(k, v))
    #fw.write(sorted(indegreeDic.items(), key=operator.itemgetter(1), reverse=True))
    fw.write("\nclass outdegree results\n")
    #fw.write(sorted(outdegreeDic.items(), key=operator.itemgetter(1), reverse=True))
    for k,v in outdegreeDic.items():
        fw.write('{}: {}\n'.format(k, v))

    print ('EXECUTION TIME: {} s'.format(time.time() - start))
        


def classInstanceDegrees(file):
    start = time.time()
    print ('START CALCULCATING CLASS INSTANCE DEGREES')
    # get all instances for the top10 classes
    f = open(file, 'r')
    lineCounter = 0
    print ('START COUNTING INSTANCES')
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
        if (p == isA and o in topClassSet):
            countInstances(s, o)
        lineCounter += 1
        if (lineCounter % lineProgress == 0):
            print ('{} million lines read'.format(lineCounter / 1000000))
            # if (lineCounter > 5000):
            #    break
    f.close()
    print ('DONE COUNTING INSTANCES')
    for k, v in instanceSetAllDict.items():
        print ('set for {}, #instances: {}'.format(k, len(v.getSet())))

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
    fw.write('class, instance count, class indegree, class outdegree, classInstanceIndegreeMin,	classInstanceIndegreeAvg, classInstanceIndegreeMedian, classInstanceIndegreeMax, classInstanceOutdegreeMin,	classInstanceOutdegreeAvg, classInstanceOutdegreeMedian, classInstanceOutdegreeMax\n')
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

        fw.write('{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}\n'.format(
                        uri,
                        instanceCount,
                        indegreeResult,
                        outdegreeResult,
                        ciAll[uri].getMin(),
                        ciAll[uri].getAvg(),
                        ciAll[uri].getMed(),
                        ciAll[uri].getMax(),
                        coAll[uri].getMin(),
                        coAll[uri].getAvg(),
                        coAll[uri].getMed(),
                        coAll[uri].getMax()
        ))
    fw.close()
    print ('DONE WRITING FINAL CSV TO allDegrees.csv')

try:
    print('START')
    classDegrees(readFile)

    classInstanceDegrees(readFile)

    createFinalCSV()
    print ('DONE WITH PROGRAM')
except:
    print ('ERROR')