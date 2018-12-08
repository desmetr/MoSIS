from pypdevs.DEVS import *
from pypdevs.simulator import Simulator

### Model
class TrainTrafficSystem(object):
	def __init__(self):
        CoupledDEVS.__init__(self, "system")
		
### Experiment
sim = Simulator(TrainTrafficSystem())
sim.setVerbose()
sim.setTerminationTime(1000)
sim.setClassicDEVS()
sim.simulate()