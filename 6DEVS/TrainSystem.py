from pypdevs.DEVS import *
from pypdevs.simulator import Simulator
from Generator import *
from Queue import *
from RailwaySegment import *

### Model
class TrainTrafficSystem(CoupledDEVS):
	def __init__(self):
		CoupledDEVS.__init__(self, "system")
		self.generator = self.addSubModel(Generator(IATMin=1, IATMax=10, aMin=1, aMax=10))
		self.railwaysegment = self.addSubModel(RailwaySegment())
		self.connectPorts(self.generator.qSend, self.railwaysegment.qRecv)
		self.connectPorts(self.railwaysegment.qSack, self.generator.qRack)
		self.connectPorts(self.generator.trainOut, self.railwaysegment.trainIn)
		
### Experiment
sim = Simulator(TrainTrafficSystem())
sim.setVerbose()
sim.setTerminationTime(5.0)
sim.setClassicDEVS()
sim.simulate()