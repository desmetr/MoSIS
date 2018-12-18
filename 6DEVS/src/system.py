from pypdevs.DEVS import *
from pypdevs.simulator import Simulator

from generator import GeneratorSubmodel, Queue
from railwaySegment import RailwaySegment
from collector import Collector

class TrainTrafficSystem(CoupledDEVS):
	def __init__(self):
		CoupledDEVS.__init__(self, "system")
		generator = self.addSubModel(GeneratorSubmodel(IATMin=1, IATMax=2, aMin=1, aMax=10, vMax=400/3.6))
		queue = self.addSubModel(Queue())
		railwaySegment = self.addSubModel(RailwaySegment(L=1100))
		collector = self.addSubModel(Collector())

		#Generator to Queue
		self.connectPorts(generator.outport, queue.inport)

		#Queue to RailwaySegment
		self.connectPorts(queue.qSend, railwaySegment.qRecv)
		self.connectPorts(railwaySegment.qSack, queue.qRack)
		self.connectPorts(queue.trainOut, railwaySegment.trainIn)

		# RailwaySegment to Collector
		self.connectPorts(railwaySegment.qSend, collector.qRecv)
		self.connectPorts(collector.qSack, railwaySegment.qRack)
		self.connectPorts(railwaySegment.trainOut, collector.trainIn)


### Experiment
system = TrainTrafficSystem()
sim = Simulator(system)
sim.setVerbose()
sim.setTerminationTime(35.0)
sim.setClassicDEVS()
sim.simulate()

# print system.collector.data()
