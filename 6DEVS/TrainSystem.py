from pypdevs.DEVS import *
from pypdevs.simulator import Simulator
from Generator import *
from Collector import *
from Queue import *
from RailwaySegment import *

### Model
class TrainTrafficSystem1(CoupledDEVS):
	def __init__(self):
		CoupledDEVS.__init__(self, "system")
		generator = self.addSubModel(Generator(IATMin=1, IATMax=10, aMin=1, aMax=10))
		railwaySegment = self.addSubModel(RailwaySegment(L=2000))
		collector = self.addSubModel(Collector())

		# Generator to RailwaySegment
		self.connectPorts(generator.qSend, railwaySegment.qRecv)
		self.connectPorts(railwaySegment.qSack, generator.qRack)
		self.connectPorts(generator.trainOut, railwaySegment.trainIn)

		# RailwaySegment to Collector
		self.connectPorts(railwaySegment.qSend, collector.qRecv)
		self.connectPorts(collector.qSack, railwaySegment.qRack)
		self.connectPorts(railwaySegment.trainOut, collector.trainIn)

		self.collector = collector

class TrainTrafficSystem2(CoupledDEVS):
	def __init__(self):
		CoupledDEVS.__init__(self, "system")
		generator = self.addSubModel(Generator(IATMin=1, IATMax=10, aMin=1, aMax=10))
		railwaySegment1 = self.addSubModel(RailwaySegment(L=2000))
		railwaySegment2 = self.addSubModel(RailwaySegment(L=3000))
		collector = self.addSubModel(Collector())

		# Generator to RailwaySegment1
		self.connectPorts(generator.qSend, railwaySegment1.qRecv)
		self.connectPorts(railwaySegment1.qSack, generator.qRack)
		self.connectPorts(generator.trainOut, railwaySegment1.trainIn)

		# RailwaySegment1 to RailwaySegment2
		self.connectPorts(railwaySegment1.qSend, railwaySegment2.qRecv)
		self.connectPorts(railwaySegment2.qSack, railwaySegment1.qRack)
		self.connectPorts(railwaySegment1.trainOut, railwaySegment2.trainIn)

		# RailwaySegment to Collector
		self.connectPorts(railwaySegment2.qSend, collector.qRecv)
		self.connectPorts(collector.qSack, railwaySegment2.qRack)
		self.connectPorts(railwaySegment2.trainOut, collector.trainIn)

		self.collector = collector
		
### Experiment
system1 = TrainTrafficSystem1()
system2 = TrainTrafficSystem2()

sim = Simulator(system1)
# sim = Simulator(system2)

sim.setVerbose()
sim.setTerminationTime(2)
sim.setClassicDEVS()
sim.simulate()

# print system.collector.data()
