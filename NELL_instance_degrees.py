# class instances indegree & outdegree for consolidated list

import os
import time
import string
import itertools
import re
import operator
import numpy

start = time.time()

nell = 'NELL/NELL.08m.995.esv.csv'
ontology = 'NELL/NELL.08m.995.ontology.csv'

lineProgress = 1000000

topClasses = set()
topClasses.update(['concept:visualizableobject',
                   'concept:visualizablething',
                   'concept:species',
                   'concept:humanagent',
                   'concept:agent',
                   'concept:person',
                   'concept:athlete',
                   'concept:actor',
                   'concept:director',
                   'concept:celebrity',
                   'concept:male',
                   'concept:ethnicgroup',
                   'concept:governmentorganization',
                   'concept:company',
                   'concept:location',
                   'concept:geopoliticalentity',
                   'concept:geopoliticallocation',
                   'concept:city',
                   'concept:street',
                   'concept:beach',
                   'concept:skiarea',
                   'concept:river',
                   'concept:airport',
                   'concept:abstractthing',
                   'concept:creativework',
                   'concept:time',
                   'concept:politician',
                   'concept:politicalparty',
                   'concept:musicalbum',
                   'concept:musicsong',
                   'concept:movie',
                   'concept:book',
                   'concept:software',
                   'concept:sport',
                   'concept:event',
                   'concept:militaryeventtype',
                   'concept:militaryconflict',
                   'concept:sportsevent',
                   'concept:country',
                   'concept:building',
                   'concept:chemical',
                   'concept:planet',
                   'concept:vehicle'
                   ])

# sets for each class to hold all instances
allInstances = set()
instancesVisualizableObject = set()
instancesVisualizableThing = set()
instancesSpecies = set()
instancesHumanAgent = set()
instancesAgent = set()
instancesPerson = set()
instancesAthlete = set()
instancesActor = set()
instancesDirector = set()
instancesCelebrity = set()
instancesMale = set()
instancesEthnicGroup = set()
instancesGovOrg = set()
instancesCompany = set()
instancesLocation = set()
instancesGeopoliticalEntity = set()
instancesGeopoliticalLocation = set()
instancesCity = set()
instancesStreet = set()
instancesBeach = set()
instancesSkiArea = set()
instancesRiver = set()
instancesAirport = set()
instancesAbstractThing = set()
instancesCreativeWork = set()
instancesTime = set()
instancesPolitician = set()
instancesPoliticalParty = set()
instancesMusicAlbum = set()
instancesMusicSong = set()
instancesMovie = set()
instancesBook = set()
instancesSoftware = set()
instancesSport = set()
instancesEvent = set()
instancesMilitaryEventType = set()
instancesMilitaryConflict = set()
instancesSportsEvent = set()
instancesCountry = set()
instancesBuilding = set()
instancesChemical = set()
instancesPlanet = set()
instancesVehicle = set()


class ClassInstances:
    def __init__(self, name):
        self.name = name
        self.allClassInstances = set()
        self.countDict = dict()
        self.min = 9999999
        self.max = 0
        self.avg = 0.0
        self.median = 0.0

    def addInstance(self, instance):
        if instance in self.allClassInstances:
            self.countDict[instance] += 1
        else:
            self.countDict[instance] = 1
            self.allClassInstances.add(instance)

    def calculateDegrees(self):
        if (len(self.countDict) != 0):
            allValueList = []
            for k, v in self.countDict.iteritems():
                allValueList.append(v)
                if (v < self.min):
                    self.min = v
                if (v > self.max):
                    self.max = v
                self.avg += v
            self.avg = self.avg / len(self.countDict)
            self.median = numpy.median(numpy.array(allValueList))

    def printResults(self):
        print ('{}'.format(self.name))
        print ('min: {}, avg: {}, median: {}, max: {}'.format(self.min, self.avg, self.median, self.max))


# instantiation of all class instances for indegree
visualizableObjectCi = ClassInstances('class instances indegree for VisualizableObject')
visualizableThingCi = ClassInstances('class instances indegree for VisualizableThing')
speciesCi = ClassInstances('class instances indegree for species')
humanAgentCi = ClassInstances('class instances indegree for humanagent')
agentCi = ClassInstances('class instances indegree for agent')
personCi = ClassInstances('class instances indegree for person')
athleteCi = ClassInstances('class instances indegree for athlete')
actorCi = ClassInstances('class instances indegree for actor')
directorCi = ClassInstances('class instances indegree for director')
celebrityCi = ClassInstances('class instances indegree for celebrity')
maleCi = ClassInstances('class instances indegree for male')
ethnicGroupCi = ClassInstances('class instances indegree for ethnicGroup')
govOrgCi = ClassInstances('class instances indegree for governmentOrganization')
companyCi = ClassInstances('class instances indegree for company')
locationCi = ClassInstances('class instances indegree for location')
geopoliticalEntityCi = ClassInstances('class instances indegree for geopoloticalEntity')
geopoliticalLocationCi = ClassInstances('class instances indegree for geopoliticalLocation')
cityCi = ClassInstances('class instances indegree for city')
streetCi = ClassInstances('class instances indegree for street')
beachCi = ClassInstances('class instances indegree for beach')
skiAreaCi = ClassInstances('class instances indegree for skiarea')
riverCi = ClassInstances('class instances indegree for river')
airportCi = ClassInstances('class instances indegree for airport')
abstractThingCi = ClassInstances('class instances indegree for abstractThing')
creativeWorkCi = ClassInstances('class instances indegree for creativeWork')
timeCi = ClassInstances('class instances indegree for time')
politicianCi = ClassInstances('class instances indegree for politician')
politicalPartyCi = ClassInstances('class instances indegree for politicalParty')
musicAlbumCi = ClassInstances('class instances indegree for musicAlbum')
musicSongCi = ClassInstances('class instances indegree for musicSong')
movieCi = ClassInstances('class instances indegree for movie')
bookCi = ClassInstances('class instances indegree for book')
softwareCi = ClassInstances('class instances indegree for software')
sportCi = ClassInstances('class instances indegree for sport')
eventCi = ClassInstances('class instances indegree for event')
militaryEventTypeCi = ClassInstances('class instances indegree for militaryEventType')
militaryConflictCi = ClassInstances('class instances indegree for militaryConflict')
sportsEventCi = ClassInstances('class instances indegree for sportsEvent')
countryCi = ClassInstances('class instances indegree for country')
buildingCi = ClassInstances('class instances indegree for building')
chemicalCi = ClassInstances('class instances indegree for chemical')
planetCi = ClassInstances('class instances indegree for planet')
vehicleCi = ClassInstances('class instances indegree for vehicle')

# list with references to all indegree classes
classInstancesIndegree = [visualizableObjectCi, visualizableThingCi, speciesCi, humanAgentCi, agentCi, personCi,
                          athleteCi, actorCi, directorCi, celebrityCi, maleCi, ethnicGroupCi, govOrgCi, companyCi,
                          locationCi, geopoliticalEntityCi, geopoliticalLocationCi, cityCi, streetCi, beachCi,
                          skiAreaCi, riverCi, airportCi, abstractThingCi, creativeWorkCi, timeCi, politicianCi,
                          politicalPartyCi, musicAlbumCi, musicSongCi, movieCi, bookCi, softwareCi, sportCi, eventCi,
                          militaryEventTypeCi, militaryConflictCi, sportsEventCi, countryCi, buildingCi, chemicalCi,
                          planetCi, vehicleCi]

# instantiation of all class instances for outdegree
visualizableObjectCo = ClassInstances('class instances outdegree for VisualizableObject')
visualizableThingCo = ClassInstances('class instances outdegree for VisualizableThing')
speciesCo = ClassInstances('class instances outdegree for species')
humanAgentCo = ClassInstances('class instances outdegree for humanagent')
agentCo = ClassInstances('class instances outdegree for agent')
personCo = ClassInstances('class instances outdegree for person')
athleteCo = ClassInstances('class instances outdegree for athlete')
actorCo = ClassInstances('class instances outdegree for actor')
directorCo = ClassInstances('class instances outdegree for director')
celebrityCo = ClassInstances('class instances outdegree for celebrity')
maleCo = ClassInstances('class instances outdegree for male')
ethnicGroupCo = ClassInstances('class instances outdegree for ethnicGroup')
govOrgCo = ClassInstances('class instances outdegree for governmentOrganization')
companyCo = ClassInstances('class instances outdegree for company')
locationCo = ClassInstances('class instances outdegree for location')
geopoliticalEntityCo = ClassInstances('class instances outdegree for geopoloticalEntity')
geopoliticalLocationCo = ClassInstances('class instances outdegree for geopoliticalLocation')
cityCo = ClassInstances('class instances outdegree for city')
streetCo = ClassInstances('class instances outdegree for street')
beachCo = ClassInstances('class instances outdegree for beach')
skiAreaCo = ClassInstances('class instances outdegree for skiarea')
riverCo = ClassInstances('class instances outdegree for river')
airportCo = ClassInstances('class instances outdegree for airport')
abstractThingCo = ClassInstances('class instances outdegree for abstractThing')
creativeWorkCo = ClassInstances('class instances outdegree for creativeWork')
timeCo = ClassInstances('class instances outdegree for time')
politicianCo = ClassInstances('class instances outdegree for politician')
politicalPartyCo = ClassInstances('class instances outdegree for politicalParty')
musicAlbumCo = ClassInstances('class instances outdegree for musicAlbum')
musicSongCo = ClassInstances('class instances outdegree for musicSong')
movieCo = ClassInstances('class instances outdegree for movie')
bookCo = ClassInstances('class instances outdegree for book')
softwareCo = ClassInstances('class instances outdegree for software')
sportCo = ClassInstances('class instances outdegree for sport')
eventCo = ClassInstances('class instances outdegree for event')
militaryEventTypeCo = ClassInstances('class instances outdegree for militaryEventType')
militaryConflictCo = ClassInstances('class instances outdegree for militaryConflict')
sportsEventCo = ClassInstances('class instances outdegree for sportsEvent')
countryCo = ClassInstances('class instances outdegree for country')
buildingCo = ClassInstances('class instances outdegree for building')
chemicalCo = ClassInstances('class instances outdegree for chemical')
planetCo = ClassInstances('class instances outdegree for planet')
vehicleCo = ClassInstances('class instances outdegree for vehicle')

# list with references to all outdegree classes
classInstancesOutdegree = [visualizableObjectCo, visualizableThingCo, speciesCo, humanAgentCo, agentCo, personCo,
                           athleteCo, actorCo, directorCo, celebrityCo, maleCo, ethnicGroupCo, govOrgCo, companyCo,
                           locationCo, geopoliticalEntityCo, geopoliticalLocationCo, cityCo, streetCo, beachCo,
                           skiAreaCo, riverCo, airportCo, abstractThingCo, creativeWorkCo, timeCo, politicianCo,
                           politicalPartyCo, musicAlbumCo, musicSongCo, movieCo, bookCo, softwareCo, sportCo, eventCo,
                           militaryEventTypeCo, militaryConflictCo, sportsEventCo, countryCo, buildingCo, chemicalCo,
                           planetCo, vehicleCi]


def getInstances(splitted_line):
    word_position = 0
    for word in splitted_line:
        if (word_position == 0):  # Entity
            entity = word
        elif (word_position == 1):  # Relation
            relation = word
        elif (word_position == 2):  # Value
            value = word
            if (relation == 'generalizations' and value in topClasses):
                # check for specific class and add instance to the class set
                if (value == 'concept:visualizableobject'):
                    if entity not in instancesVisualizableObject:
                        instancesVisualizableObject.add(entity)
                    return
                elif (value == 'concept:visualizablething'):
                    if entity not in instancesVisualizableThing:
                        instancesVisualizableThing.add(entity)
                    return
                elif (value == 'concept:species'):
                    if entity not in instancesSpecies:
                        instancesSpecies.add(entity)
                    return
                elif (value == 'concept:humanagent'):
                    if entity not in instancesHumanAgent:
                        instancesHumanAgent.add(entity)
                    return
                elif (value == 'concept:agent'):
                    if entity not in instancesAgent:
                        instancesAgent.add(entity)
                    return
                elif (value == 'concept:person'):
                    if entity not in instancesPerson:
                        instancesPerson.add(entity)
                    return
                elif (value == 'concept:athlete'):
                    if entity not in instancesAthlete:
                        instancesAthlete.add(entity)
                    return
                elif (value == 'concept:actor'):
                    if entity not in instancesActor:
                        instancesActor.add(entity)
                    return
                elif (value == 'concept:director'):
                    if entity not in instancesDirector:
                        instancesDirector.add(entity)
                    return
                elif (value == 'concept:celebrity'):
                    if entity not in instancesCelebrity:
                        instancesCelebrity.add(entity)
                    return
                elif (value == 'concept:male'):
                    if entity not in instancesMale:
                        instancesMale.add(entity)
                    return
                elif (value == 'concept:ethnicgroup'):
                    if entity not in instancesEthnicGroup:
                        instancesEthnicGroup.add(entity)
                    return
                elif (value == 'concept:governmentorganization'):
                    if entity not in instancesGovOrg:
                        instancesGovOrg.add(entity)
                    return
                elif (value == 'concept:company'):
                    if entity not in instancesCompany:
                        instancesCompany.add(entity)
                    return
                elif (value == 'concept:location'):
                    if entity not in instancesLocation:
                        instancesLocation.add(entity)
                    return
                elif (value == 'concept:geopoliticalentity'):
                    if entity not in instancesGeopoliticalEntity:
                        instancesGeopoliticalEntity.add(entity)
                    return
                elif (value == 'concept:geopoliticallocation'):
                    if entity not in instancesGeopoliticalLocation:
                        instancesGeopoliticalLocation.add(entity)
                    return
                elif (value == 'concept:city'):
                    if entity not in instancesCity:
                        instancesCity.add(entity)
                    return
                elif (value == 'concept:street'):
                    if entity not in instancesStreet:
                        instancesStreet.add(entity)
                    return
                elif (value == 'concept:beach'):
                    if entity not in instancesBeach:
                        instancesBeach.add(entity)
                    return
                elif (value == 'concept:skiarea'):
                    if entity not in instancesSkiArea:
                        instancesSkiArea.add(entity)
                    return
                elif (value == 'concept:river'):
                    if entity not in instancesRiver:
                        instancesRiver.add(entity)
                    return
                elif (value == 'concept:airport'):
                    if entity not in instancesAirport:
                        instancesAirport.add(entity)
                    return
                elif (value == 'concept:abstractthing'):
                    if entity not in instancesAbstractThing:
                        instancesAbstractThing.add(entity)
                    return
                elif (value == 'concept:creativework'):
                    if entity not in instancesCreativeWork:
                        instancesCreativeWork.add(entity)
                    return
                elif (value == 'concept:time'):
                    if entity not in instancesTime:
                        instancesTime.add(entity)
                    return
                elif (value == 'concept:politician'):
                    if entity not in instancesPolitician:
                        instancesPolitician.add(entity)
                    return
                elif (value == 'concept:politicalparty'):
                    if entity not in instancesPoliticalParty:
                        instancesPoliticalParty.add(entity)
                    return
                elif (value == 'concept:musicalbum'):
                    if entity not in instancesMusicAlbum:
                        instancesMusicAlbum.add(entity)
                    return
                elif (value == 'concept:musicsong'):
                    if entity not in instancesMusicSong:
                        instancesMusicSong.add(entity)
                    return
                elif (value == 'concept:movie'):
                    if entity not in instancesMovie:
                        instancesMovie.add(entity)
                    return
                elif (value == 'concept:book'):
                    if entity not in instancesBook:
                        instancesBook.add(entity)
                    return
                elif (value == 'concept:software'):
                    if entity not in instancesSoftware:
                        instancesSoftware.add(entity)
                    return
                elif (value == 'concept:sport'):
                    if entity not in instancesSport:
                        instancesSport.add(entity)
                    return
                elif (value == 'concept:event'):
                    if entity not in instancesEvent:
                        instancesEvent.add(entity)
                    return
                elif (value == 'concept:militaryeventtype'):
                    if entity not in instancesMilitaryEventType:
                        instancesMilitaryEventType.add(entity)
                    return
                elif (value == 'concept:militaryconflict'):
                    if entity not in instancesMilitaryConflict:
                        instancesMilitaryConflict.add(entity)
                    return
                elif (value == 'concept:sportsevent'):
                    if entity not in instancesSportsEvent:
                        instancesSportsEvent.add(entity)
                    return
                elif (value == 'concept:country'):
                    if entity not in instancesCountry:
                        instancesCountry.add(entity)
                    return
                elif (value == 'concept:building'):
                    if entity not in instancesBuilding:
                        instancesBuilding.add(entity)
                    return
                elif (value == 'concept:chemical'):
                    if entity not in instancesChemical:
                        instancesChemical.add(entity)
                    return
                elif (value == 'concept:planet'):
                    if entity not in instancesPlanet:
                        instancesPlanet.add(entity)
                    return
                elif (value == 'concept:vehicle'):
                    if entity not in instancesVehicle:
                        instancesVehicle.add(entity)
                    return
                return
            else:  # NOT relation == 'generalizations' and value in topClasses
                return
        elif (word_position > 2):
            return
        word_position += 1


def countInstanceDegrees(splitted_line):
    word_position = 0
    for word in splitted_line:
        if (word_position == 0):  # Entity: OUTDEGREE
            entity = word
            if entity in allInstances:
                if entity in instancesVisualizableObject:
                    visualizableObjectCo.addInstance(entity)
                elif entity in instancesVisualizableThing:
                    visualizableThingCo.addInstance(entity)
                elif entity in instancesSpecies:
                    speciesCo.addInstance(entity)
                elif entity in instancesHumanAgent:
                    humanAgentCo.addInstance(entity)
                elif entity in instancesAgent:
                    agentCo.addInstance(entity)
                elif entity in instancesPerson:
                    personCo.addInstance(entity)
                elif entity in instancesAthlete:
                    athleteCo.addInstance(entity)
                elif entity in instancesActor:
                    actorCo.addInstance(entity)
                elif entity in instancesDirector:
                    directorCo.addInstance(entity)
                elif entity in instancesCelebrity:
                    celebrityCo.addInstance(entity)
                elif entity in instancesMale:
                    maleCo.addInstance(entity)
                elif entity in instancesEthnicGroup:
                    ethnicGroupCo.addInstance(entity)
                elif entity in instancesGovOrg:
                    govOrgCo.addInstance(entity)
                elif entity in instancesCompany:
                    companyCo.addInstance(entity)
                elif entity in instancesLocation:
                    locationCo.addInstance(entity)
                elif entity in instancesGeopoliticalEntity:
                    geopoliticalEntityCo.addInstance(entity)
                elif entity in instancesGeopoliticalLocation:
                    geopoliticalLocationCo.addInstance(entity)
                elif entity in instancesCity:
                    cityCo.addInstance(entity)
                elif entity in instancesStreet:
                    streetCo.addInstance(entity)
                elif entity in instancesBeach:
                    beachCo.addInstance(entity)
                elif entity in instancesSkiArea:
                    skiAreaCo.addInstance(entity)
                elif entity in instancesRiver:
                    riverCo.addInstance(entity)
                elif entity in instancesAirport:
                    airportCo.addInstance(entity)
                elif entity in instancesAbstractThing:
                    abstractThingCo.addInstance(entity)
                elif entity in instancesCreativeWork:
                    creativeWorkCo.addInstance(entity)
                elif entity in instancesTime:
                    timeCo.addInstance(entity)
                elif entity in instancesPolitician:
                    politicianCo.addInstance(entity)
                elif entity in instancesPoliticalParty:
                    politicalPartyCo.addInstance(entity)
                elif entity in instancesMusicAlbum:
                    musicAlbumCo.addInstance(entity)
                elif entity in instancesMusicSong:
                    musicSongCo.addInstance(entity)
                elif entity in instancesMovie:
                    movieCo.addInstance(entity)
                elif entity in instancesBook:
                    bookCo.addInstance(entity)
                elif entity in instancesSoftware:
                    softwareCo.addInstance(entity)
                elif entity in instancesSport:
                    sportCo.addInstance(entity)
                elif entity in instancesEvent:
                    eventCo.addInstance(entity)
                elif entity in instancesMilitaryEventType:
                    militaryEventTypeCo.addInstance(entity)
                elif entity in instancesMilitaryConflict:
                    militaryConflictCo.addInstance(entity)
                elif entity in instancesSportsEvent:
                    sportsEventCo.addInstance(entity)
                elif entity in instancesCountry:
                    countryCo.addInstance(entity)
                elif entity in instancesBuilding:
                    buildingCo.addInstance(entity)
                elif entity in instancesChemical:
                    chemicalCo.addInstance(entity)
                elif entity in instancesPlanet:
                    planetCo.addInstance(entity)
                elif entity in instancesVehicle:
                    vehicleCo.addInstance(entity)
        elif (word_position == 2):  # Value: INDEGREE
            value = word
            if value in allInstances:
                if value in instancesVisualizableObject:
                    visualizableObjectCi.addInstance(value)
                elif value in instancesVisualizableThing:
                    visualizableThingCi.addInstance(value)
                elif value in instancesSpecies:
                    speciesCi.addInstance(value)
                elif value in instancesHumanAgent:
                    humanAgentCi.addInstance(value)
                elif value in instancesAgent:
                    agentCi.addInstance(value)
                elif value in instancesPerson:
                    personCi.addInstance(value)
                elif value in instancesAthlete:
                    athleteCi.addInstance(value)
                elif value in instancesActor:
                    actorCi.addInstance(value)
                elif value in instancesDirector:
                    directorCi.addInstance(value)
                elif value in instancesCelebrity:
                    celebrityCi.addInstance(value)
                elif value in instancesMale:
                    maleCi.addInstance(value)
                elif value in instancesEthnicGroup:
                    ethnicGroupCi.addInstance(value)
                elif value in instancesGovOrg:
                    govOrgCi.addInstance(value)
                elif value in instancesCompany:
                    companyCi.addInstance(value)
                elif value in instancesLocation:
                    locationCi.addInstance(value)
                elif value in instancesGeopoliticalEntity:
                    geopoliticalEntityCi.addInstance(value)
                elif value in instancesGeopoliticalLocation:
                    geopoliticalLocationCi.addInstance(value)
                elif value in instancesCity:
                    cityCi.addInstance(value)
                elif value in instancesStreet:
                    streetCi.addInstance(value)
                elif value in instancesBeach:
                    beachCi.addInstance(value)
                elif value in instancesSkiArea:
                    skiAreaCi.addInstance(value)
                elif value in instancesRiver:
                    riverCi.addInstance(value)
                elif value in instancesAirport:
                    airportCi.addInstance(value)
                elif value in instancesAbstractThing:
                    abstractThingCi.addInstance(value)
                elif value in instancesCreativeWork:
                    creativeWorkCi.addInstance(value)
                elif value in instancesTime:
                    timeCi.addInstance(value)
                elif value in instancesPolitician:
                    politicianCi.addInstance(value)
                elif value in instancesPoliticalParty:
                    politicalPartyCi.addInstance(value)
                elif value in instancesMusicAlbum:
                    musicAlbumCi.addInstance(value)
                elif value in instancesMusicSong:
                    musicSongCi.addInstance(value)
                elif value in instancesMovie:
                    movieCi.addInstance(value)
                elif value in instancesBook:
                    bookCi.addInstance(value)
                elif value in instancesSoftware:
                    softwareCi.addInstance(value)
                elif value in instancesSport:
                    sportCi.addInstance(value)
                elif value in instancesEvent:
                    eventCi.addInstance(value)
                elif value in instancesMilitaryEventType:
                    militaryEventTypeCi.addInstance(value)
                elif value in instancesMilitaryConflict:
                    militaryConflictCi.addInstance(value)
                elif value in instancesSportsEvent:
                    sportsEventCi.addInstance(value)
                elif value in instancesCountry:
                    countryCi.addInstance(value)
                elif value in instancesBuilding:
                    buildingCi.addInstance(value)
                elif value in instancesChemical:
                    chemicalCi.addInstance(value)
                elif value in instancesPlanet:
                    planetCi.addInstance(value)
                elif value in instancesVehicle:
                    vehicleCi.addInstance(value)
            return  # done processing entity and value
        elif (word_position > 2):
            return
        word_position += 1


def calculateClassDegrees():
    for item in classInstancesIndegree:
        item.calculateDegrees()
    for item in classInstancesOutdegree:
        item.calculateDegrees()


def printClassDegreeResults():
    for item in classInstancesIndegree:
        item.printResults()
    for item in classInstancesOutdegree:
        item.printResults()


print('START')
try:
    # get Instances and calculate indegree&outdegree
    for i in xrange(2):
        if (i == 0):
            print ('1. run: get class instances')
        elif (i == 1):
            print ('2. run: count instance degrees')
        # read nell & ontology
        for j in xrange(2):
            if (j == 0):
                print ('read ontology')
                readFile = ontology
            elif (j == 1):
                print ('read NELL')
                readFile = nell
            fn = open(readFile, 'r')
            lineCounter = 0
            for line in fn:
                if (lineCounter != 0):
                    splitted_line = line.rstrip('\n').split('\t')
                    if (i == 0):
                        getInstances(splitted_line)
                    elif (i == 1):
                        countInstanceDegrees(splitted_line)
                    else:
                        calculateClassDegrees(splitted_line)
                lineCounter += 1
                if (lineCounter % lineProgress == 0):
                    print ('{} lines read'.format(lineCounter))
            fn.close()
        if (i == 0):
            # create set of all instances
            # allInstances = instancesBeach|instancesActor|instancesGovOrg|instancesDirector|instancesSkiArea|instancesStreet|instancesRiver|instancesAirport|instancesCelebrity|instancesMale
            allInstances = instancesVisualizableObject | instancesVisualizableThing | instancesSpecies | instancesHumanAgent | instancesAgent | instancesPerson | instancesAthlete | instancesActor | instancesDirector | instancesCelebrity | instancesMale | instancesEthnicGroup | instancesGovOrg | instancesCompany | instancesLocation | instancesGeopoliticalEntity | instancesGeopoliticalLocation | instancesCity | instancesStreet | instancesBeach | instancesSkiArea | instancesRiver | instancesAirport | instancesAbstractThing | instancesCreativeWork | instancesTime | instancesPolitician | instancesPoliticalParty | instancesMusicAlbum | instancesMusicSong | instancesMovie | instancesBook | instancesSoftware | instancesSport | instancesEvent | instancesMilitaryEventType | instancesMilitaryConflict | instancesSportsEvent | instancesCountry | instancesBuilding | instancesChemical | instancesPlanet | instancesVehicle
    # calculate min, avg, max class degrees
    print ('calculate class degrees')
    calculateClassDegrees()

    print ('DONE')
    print('RESULTS')
    printClassDegreeResults()
except:
    print('ERROR')

end = time.time()
print ('EXECUTION TIME: {} s'.format(end - start))