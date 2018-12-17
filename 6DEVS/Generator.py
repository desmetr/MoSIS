from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY
from Train import *
from Query import *
import random

class GeneratorState:
	def __init__(self):
		self.current_time = 0.0

class Generator(AtomicDEVS):
	def __init__(self, IATMin, IATMax, aMin, aMax):
		AtomicDEVS.__init__(self, "Generator")
		self.state = "sending"

		self.IATMin = IATMin
		self.IATMax = IATMax
		self.aMin = aMin
		self.aMax = aMax
		
		self.qRack = self.addInPort("qRack")
		self.qSend = self.addOutPort("qSend")
		self.trainOut = self.addOutPort("trainOut")

		self.numberOfTrainsOutput = 0

		self.queue = []
		self.currentLight = "green"

		self.currentTime = 0
	
	def intTransition(self):
		return {"sending": "waiting",
				"waiting": "sending"}[self.state]

	def timeAdvance(self):
		# TODO: correcte timeAdvance?
		# self.currentTime += 1	# TODO
		if self.state == "sending":
			return 1
		elif self.state == "waiting":
			return 0
		elif len(self.queue) == 0:
			return INFINITY

	def outputFnc(self):
		newIAT = random.randint(self.IATMin, self.IATMax - 1)
		newA = random.randint(self.aMin, self.aMax - 1)
		newID = self.numberOfTrainsOutput
		creationTime = self.currentTime # TODO moet er nog iets bij?
		self.numberOfTrainsOutput = newID
		
		newTrain = Train(newID, newA, newIAT, creationTime)
		self.numberOfTrainsOutput += 1

		trainToOutput = None
		print "queue generator: ", self.queue

		if self.state == "waiting":
			self.queue.append(newTrain)

		elif self.state == "sending":
			if len(self.queue) > 1:
				trainToOutput = self.queue.pop(0)
			elif trainToOutput == None:
				trainToOutput = newTrain

		return {self.trainOut: trainToOutput, self.qSend: Query("queryToEnter")}

	def extTransition(self, inputs):
		queryAck = inputs[self.qRack]
		self.currentLight = queryAck.light

		if queryAck.light == "red":
			return "waiting"
		elif queryAck.light == "green":
			return "sending"