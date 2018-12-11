from pypdevs.DEVS import *
from pypdevs.simulator import Simulator
from Generator import *
from Queue import *

### Model
class TrainTrafficSystem(CoupledDEVS):
	def __init__(self):
		CoupledDEVS.__init__(self, "system")
		self.generator = self.addSubModel(Generator(IAT_min=1, IAT_max=10, a_min=1, a_max=10))
		self.queue = self.addSubModel(Queue())
		self.connectPorts(self.generator.outport, self.queue.inport)
		
### Experiment
sim = Simulator(TrainTrafficSystem())
sim.setVerbose()
sim.setTerminationTime(5)
sim.setClassicDEVS()
sim.simulate()