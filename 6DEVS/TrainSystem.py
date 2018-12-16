from pypdevs.DEVS import *
from pypdevs.simulator import Simulator
from Generator import *
from Collector import *
from Queue import *
from RailwaySegment import *

### Model
class TrainTrafficSystem(CoupledDEVS):
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
		
### Experiment
system = TrainTrafficSystem()
sim = Simulator(system)
sim.setVerbose()
sim.setTerminationTime(5.0)
sim.setClassicDEVS()
sim.simulate()

# print system.collector.data()
