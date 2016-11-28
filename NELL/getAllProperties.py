nell = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.esv.csv'
ontology = '../../../SeminarPaper_KG_Files/NELL/NELL.08m.995.ontology.csv'
readFiles = [nell]#], ontology]
propertySet = set()

def getP(splittedLine):
    word_position = 0
    for word in splittedLine:
        if (word_position == 1):
            pred = word
        word_position += 1
    return pred

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


for file in readFiles:
    f = open(file, 'r')
    for line in f:
        splittedLine = line.rstrip('\n').split()
        s, p, o = getSPO(splittedLine)
        if p not in propertySet:
            propertySet.add(p)
        if (p == 'generalizations'):
            print o
    f.close()

#print propertySet