from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY

class RailwaySegment(AtomicDEVS):
    segmentCounter = 0

	def __init__(self, L):
        segmentCounter += 1
		AtomicDEVS.__init__(self, "RailwaySegment{}".format(segmentCounter))

		self.state = "EMPTY"#emmpty or busy
		self.L = L
		self.currentTrain = None

		self.qRecv = self.addInPort("qRecv")
		self.qSack = self.addOutPort("qSack")
		self.qSend = self.addOutPort("qSend")

		self.qRack = self.addInPort("qRack")
		self.trainIn = self.addInPort("trainIn")
		self.trainOut = self.addOutPort("trainOut")

    def timeAdvance(self):
        if self.state == "EMPTY":
            return INFINITY
        else:
            #TODO
            pass

    def intTransition(self):
		# TODO
		pass

	def extTransition(self, inputs):
        #TODO
        pass

    def outputFnc(self):
        #TODO
        pass
