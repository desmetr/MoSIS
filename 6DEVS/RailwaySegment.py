from pypdevs.DEVS import *
from Query import *

class RailwaySegment(AtomicDEVS):
	def __init__(self, L):
		AtomicDEVS.__init__(self, "RailwaySegment")
		self.state = "noTrain"
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
		return {"noTrain": "hasTrain",
				"hasTrain": "noTrain"}[self.state]

	def timeAdvance(self):
		# TODO
		return {"noTrain": self.tSolve,
				"hasTrain": 1.0}[self.state]

	def outputFnc(self):
		return {self.qSend: Query("queryToEnter"),
				self.qSack: QueryAck(self.light)}

	def extTransition(self, inputs):
		print inputs
		# state = "noTrain"
		state = self.state

		if self.trainIn in inputs:
			# print "A"
			if not inputs[self.trainIn] is None: 
				self.currentTrain = inputs[self.trainIn]
				print self.currentTrain
			state = "hasTrain"
		
		if self.qRecv in inputs: 
			# print "B"
			self.currentQuery = inputs[self.qRecv]
			if not self.currentTrain is None:
				self.light = "red"
			else:
				self.light = "green"

		if self.qRack in inputs:
			# print "C"
			self.currentQueryAck = inputs[self.qRack]
			if not self.currentTrain is None:
				if self.currentQueryAck.light == "red":
					self.currentTrain.brake()
				elif self.currentQueryAck.light == "green":
						self.tSolve = self.currentTrain.accelerate()[1]
						print self.tSolve

		return state