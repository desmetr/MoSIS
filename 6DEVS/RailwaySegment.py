from pypdevs.DEVS import *
from Query import *
import formulas

class RailwaySegment(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "RailwaySegment")
		self.L = 0
		self.currentTrain = None
		self.currentQuery = None
		self.currentQueryAck = None
		self.tSolve = 0
		self.light = "red"

		self.qRecv = self.addInPort("qRecv")
		self.qSack = self.addOutPort("qSack")
		self.qSend = self.addOutPort("qSend")
		self.qRack = self.addInPort("qRack")
		self.trainIn = self.addInPort("trainIn")
		self.trainOut = self.addOutPort("trainOut")

	def intTransition(self):
		# TODO
		state = self.state
		return {"noTrain": "hasTrain",
				"hasTrain": "noTrain"}[state]

	def timeAdvance(self):
		# TODO
		# return {"trainLeaves": self.tSolve}[0]
		return 1.0

	def outputFnc(self):
		return {self.qSend: Query("queryToEnter"), 
				self.qSack: QueryAck(self.light)}

	def extTransition(self):
		self.currentTrain = inputs[self.trainIn]
		self.currentQuery = inputs[self.qRecv]
		self.currentQueryAck = inputs[self.qRack]

		# TODO: t_poll? self.L?
		if self.currentQueryAck.light == "green":
			# accelerate as fast as possible
			(v, self.tSolve) = acceleration_formula(self.currentTrain.v, 100, self.L, self.currentTrain.aMax)
		elif self.currentQueryAck.light == "red":
			# gradually brake
			(newV, xTravelled) = brake_formula(self.currentTrain.v, 1, self.L)

