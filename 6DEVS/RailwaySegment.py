from pypdevs.DEVS import *
from Query import *

class RailwaySegment(AtomicDEVS):
	def __init__(self, L):
		AtomicDEVS.__init__(self, "RailwaySegment")
		self.state = "emptyAndGreen"
		self.L = L
		self.currentTrain = None
		self.currentQuery = None
		self.currentQueryAck = None
		self.tSolve = 0
		self.light = "green"

		self.qRecv = self.addInPort("qRecv")
		self.qSack = self.addOutPort("qSack")
		self.qSend = self.addOutPort("qSend")
		self.qRack = self.addInPort("qRack")
		self.trainIn = self.addInPort("trainIn")
		self.trainOut = self.addOutPort("trainOut")

	def intTransition(self):
		# TODO
		# return {"idle": "hasTrainAndGreen",
		return {"emptyAndRed": "hasTrainAndGreen",
				"hasTrainAndGreen": "emptyAndRed",
				"emptyAndGreen": "hasTrainAndRed",
				"hasTrainAndRed": "emptyAndGreen"}[self.state]

	def timeAdvance(self):
		# TODO
		return {"emptyAndRed": 1,
				"emptyAndGreen": 1,
				"hasTrainAndRed": 1,
				"hasTrainAndGreen": self.tSolve}[self.state]

	def outputFnc(self):
		trainToOutput = None
		if self.state == "hasTrainAndGreen":
			trainToOutput = self.currentTrain
			self.currentTrain = None

		return {self.qSend: Query("queryToEnter"),
				self.qSack: QueryAck(self.light),
				self.trainOut: trainToOutput}

	def extTransition(self, inputs):
		state = self.state

		if self.trainIn in inputs:
			if not inputs[self.trainIn] is None: 
				self.currentTrain = inputs[self.trainIn]
				self.currentTrain.remainingX = self.L

			state = "hasTrainAndGreen"
		
		if self.qRecv in inputs: 
			self.currentQuery = inputs[self.qRecv]

			if self.state == "hasTrainAndGreen":
				self.light = "red"
			else:
				self.light = "green"

		if self.qRack in inputs:
			self.currentQueryAck = inputs[self.qRack]

			if not self.currentTrain is None:
				if self.currentQueryAck.light == "red":
					self.currentTrain.brake(self.L)
					state = "hasTrainAndRed"
				elif self.currentQueryAck.light == "green":
					self.tSolve = self.currentTrain.accelerate(self.L)
					state = "hasTrainAndGreen"
					self.light = "red"	# nodig?

		return state