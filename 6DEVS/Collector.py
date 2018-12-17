from pypdevs.DEVS import *
from Query import *		

class Collector(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "Collector")
		self.state = True
		
		self.qRecv = self.addInPort("qRecv")
		self.trainIn = self.addInPort("trainIn")
		self.qSack = self.addOutPort("qSack")

		self.trainsCollected = []

	def intTransition(self):
		pass

	def timeAdvance(self):
		print "in collector: ", self.trainsCollected
		return 1.0

	def outputFnc(self):
		return {self.qSack: QueryAck("green")}

	def extTransition(self, inputs):
		train = inputs[self.trainIn]
		
		# hier komt performance metrics

		self.trainsCollected.append(train)		