from pypdevs.DEVS import *

class CollectorState:
	def __init__(self):
		self.trains = []
		self.currentTime = 0.0		

class Collector(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "Collector")
		self.state = CollectorState()
		
		self.inport = self.addInPort("input")

	def intTransition(self):
		pass

	def timeAdvance(self):
		pass

	def outputFnc(self):
		pass

	def extTransition(self):
		# Update simulation time
		self.state.current_time += self.elapsed
		# Calulate time in queue
		train = inputs[self.inport]
		