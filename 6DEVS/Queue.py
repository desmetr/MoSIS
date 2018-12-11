from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY

class Queue(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "Queue")
		self.state = None
		self.processing_time = 1.0
		self.inport = self.addInPort("input")
		self.outport = self.addOutPort("output")

	def timeAdvance(self):
		if self.state is None:
			return INFINITY
		else:
			return self.processing_time

	def outputFnc(self):
		return {self.outport: self.state}

	def extTransition(self):
		self.state = inputs[self.inport]
		return self.state

	def intTransition(self):
		self.state = None
		return self.state