from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY
from Train import *
from Query import *
import random

class GeneratorSubmodel(AtomicDEVS):
	def __init__(self, IATMin, IATMax, aMin, aMax):
		AtomicDEVS.__init__(self, "GeneratorSubmodel")
		self.state = True

		self.outport = self.addOutPort("outport")

		self.IATMin = IATMin
		self.IATMax = IATMax
		self.aMin = aMin
		self.aMax = aMax
		self.ID = 0
		self.creationTime = 0

	def timeAdvance(self):
	    # Random interarrival time
		newIAT = random.randint(self.IATMin, self.IATMax - 1)
		self.creationTime += newIAT
		return newIAT

	def outputFnc(self):
		# Generate a train
		self.ID += 1

		newA = random.randint(self.aMin, self.aMax - 1)
		newID = self.ID
		creationTime = self.creationTime
		return {self.outport: Train(newID, newA, creationTime)}

class Queue(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "Queue")
		self.state = "EMPTY"#empty, waiting or sending

		self.inport = self.addInPort("input")

		self.qRack = self.addInPort("qRack")
		self.qSend = self.addOutPort("qSend")
		self.trainOut = self.addOutPort("trainOut")

		self.q = []

	def timeAdvance(self):
		if self.state == "EMPTY":
			return INFINITY
	  	elif self.state == "WAITING":
			return 1
		elif self.state == "SENDING":
			return 0
		else:
			raise DEVSException("unknown state {} in Queue timeAdvance".format(self.state))

	def outputFnc(self):
		if self.state == "WAITING":
			return {self.qSend: "queryToEnter"}
		elif self.state == "SENDING":
			train = q.pop(0)
			return {self.trainOut: train}
		else:
			raise DEVSException("invalid state {} in Queue outputFnc".format(self.state))

	def intTransition(self):
		if self.state == "WAITING":
			return "WAITING"
		elif self.state == "SENDING":
			if len(self.q) > 1:# >1 because we will output 1 train. TODO correct?
				return "WAITING"
			else:
				return "EMPTY"
		else:
			raise DEVSException("invalid state {} in Queue intTransition".format(self.state))


	def extTransition(self, inputs):
        # if incoming train
		if True:#TODO
			self.state = "WAITING"
			train = inputs[self.inport]
			self.q.append(train)
		# if incoming ack
		elif True:#TODO
			pass
		return self.state


class Generator(CoupledDEVS):
	def __init__(self):
		CoupledDEVS.__init__(self, "Generator")

		self.gen = self.addSubModel(GeneratorSubmodel())
		self.q   = self.addSubModel(Queue())

		self.connectPorts(self.gen.outport, self.q.inport)
