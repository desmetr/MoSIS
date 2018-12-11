from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY
from Train import *
import random

class Generator(AtomicDEVS):
	def __init__(self, IAT_min, IAT_max, a_min, a_max):
		AtomicDEVS.__init__(self, "Generator")
		self.state = True

		self.IAT_min = IAT_min
		self.IAT_max = IAT_max
		self.a_min = a_min
		self.a_max = a_max

		# One output port to generate the train. No input ports.
		self.outport = self.addOutPort("output")

		self.numberOfTrainsOutput = 0
	
	def intTransition(self):
		self.state = False
		return self.state

	def timeAdvance(self):
		# TODO: correcte timeAdvance?
		if self.state:
			return 1.0
		else:
			return INFINITY

	def outputFnc(self):
		# TODO: checken of trein op track mag, zo ja pak uit queue, anders zet in queue
		
		newIAT = random.randint(self.IAT_min, self.IAT_max - 1)
		newA = random.randint(self.a_min, self.a_max - 1)
		newID = self.numberOfTrainsOutput + 1
		self.numberOfTrainsOutput = newID
		newTrain = Train(newID, newA, newIAT)

		return {self.outport: newTrain}