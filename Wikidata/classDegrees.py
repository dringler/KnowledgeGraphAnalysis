# calculate class indegree & outdegree

import operator
import time

print ('STARTING')
start = time.time()

#readFile = '/Volumes/Samsung/wikidata_sample_from_nt.nt'
readFile = '/Volumes/Samsung/wikidata.nt'

lineProgress = 10000000

indegreeDic = {} # key:class, value: indegree
outdegreeDic = {} #key: class, value: outdegree

top10set = set()
top10set.update(['<http://www.wikidata.org/entity/Q5>', '<http://www.wikidata.org/entity/Q4167836>', '<http://www.wikidata.org/entity/Q16521>', '<http://www.wikidata.org/entity/Q4167410>', '<http://www.wikidata.org/entity/Q11266439>', '<http://www.wikidata.org/entity/Q13100073>', '<http://www.wikidata.org/entity/Q486972>', '<http://www.wikidata.org/entity/Q532>', '<http://www.wikidata.org/entity/Q79007>', '<http://www.wikidata.org/entity/Q13406463>'])

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

try:
    f = open(readFile, 'r')
    lineCounter = 0
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
        if s in top10set:
            if outdegreeDic.has_key(s):
                outdegreeDic[s] += 1
            else:
                outdegreeDic[s] = 1
        if o in top10set:
            if indegreeDic.has_key(o):
                indegreeDic[o] += 1
            else:
                indegreeDic[o] = 1
        lineCounter += 1
        if (lineCounter % lineProgress == 0):
            print ('{} million lines read'.format(lineCounter/1000000))
    f.close()
    print ('DONE')
    print ('INDEGREE RESULT:')
    print sorted(indegreeDic.items(), key=operator.itemgetter(1), reverse=True)
    print ('OUTDEGREE RESULT')
    print sorted(outdegreeDic.items(), key=operator.itemgetter(1), reverse=True)
    print ('EXECUTION TIME: {} s'.format(time.time()-start))
except:
    print ('ERROR')