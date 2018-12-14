from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY
from Train import *
import random

class GeneratorState:
	def __init__(self):
		self.current_time = 0.0

class Generator(AtomicDEVS):
	def __init__(self, IATMin, IATMax, aMin, aMax):
		AtomicDEVS.__init__(self, "Generator")
		self.state = GeneratorState()

		self.IATMin = IATMin
		self.IATMax = IATMax
		self.aMin = aMin
		self.aMax = aMax
		
		self.qRack = self.addInPort("qRack")
		self.qSend = self.addOutPort("qSend")
		self.trainOut = self.addOutPort("trainOut")

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
		creationTime = self.state.current_time # TODO moet er nog iets bij?
		self.numberOfTrainsOutput = newID
		
		newTrain = Train(newID, newA, newIAT, creationTime)
		return {self.trainOut: newTrain}