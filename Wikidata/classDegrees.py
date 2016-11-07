# calculate class indegree & outdegree

import operator
import time

print ('STARTING')
start = time.time()

#readFile = '/Volumes/Samsung/wikidata_sample_from_nt.nt'
#readFile = '/Volumes/Samsung/wikidata.nt'
readFile = 'wikidata_sample_1m.nt'

lineProgress = 10000000

indegreeDic = {} # key:class, value: indegree
outdegreeDic = {} #key: class, value: outdegree

topClassSet = set()
#topClassSet.update(['<http://www.wikidata.org/entity/Q5>', '<http://www.wikidata.org/entity/Q4167836>', '<http://www.wikidata.org/entity/Q16521>', '<http://www.wikidata.org/entity/Q4167410>', '<http://www.wikidata.org/entity/Q11266439>', '<http://www.wikidata.org/entity/Q13100073>', '<http://www.wikidata.org/entity/Q486972>', '<http://www.wikidata.org/entity/Q532>', '<http://www.wikidata.org/entity/Q79007>', '<http://www.wikidata.org/entity/Q13406463>'])
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