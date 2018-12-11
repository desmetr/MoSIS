from pypdevs.DEVS import *

class Collector(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "Collector")
		
		self.collectorIn = self.addInPort("input")

	def intTransition(self):
		pass

	def timeAdvance(self):
		pass

	def outputFnc(self):
		pass

	def extTransition(self):
		pass