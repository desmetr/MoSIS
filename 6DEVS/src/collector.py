from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY

class Collector(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "Collector")
		self.state = "WAITING"#WAITING or RESPONDING

		self.qRecv = self.addInPort("qRecv")
		self.qSack = self.addOutPort("qSack")
		self.trainIn = self.addInPort("trainIn")

		self.trainsCollected = []

	def timeAdvance(self):
		if self.state == "WAITING":
		    return INFINITY
		elif self.state == "RESPONDING":
		    return 0
		else:
		    raise DEVSException("invalid state {} in Collector timeAdvance".format(self.state))

	def outputFnc(self):
		if self.state == "RESPONDING":
		    return {self.qSack: "GREEN"}

	def intTransition(self):
	    if self.state == "RESPONDING":
	        return "WAITING"
	    else:
	        raise DEVSException("invalid state {} in Collector intTransition".format(self.state))

	def extTransition(self, inputs):
		qRecv = inputs.get(self.qRecv)
		trainIn = inputs.get(self.trainIn)

		if qRecv is not None:
		    return "RESPONDING"
		if trainIn is not None:
			currentTime = self.time_last[0] + self.elapsed
			trainIn.setArrivalTime(currentTime)
			self.trainsCollected.append(trainIn)
			print "## {} arrived at time {}".format(repr(trainIn), currentTime)
			return "WAITING"
