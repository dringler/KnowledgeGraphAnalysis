from FuXi.Syntax.InfixOWL import *

galenGraph = Graph().parse(os.path.join(os.path.dirname(__file__), 'opencyc-latest_sample.owl'))
graph=galenGraph
#for c in graph.subjects(predicate=RDF.type,object=OWL_NS.Class):
#    if isinstance(c,URIRef):
#        print Class(c,graph=graph).__repr__(True),"\n"

f = open('opencyc-latest_sample.nt', 'w')
f.write(graph.serialize(format='nt'))
f.close()
print ('DONE')
#print graph.serialize(format='n3')