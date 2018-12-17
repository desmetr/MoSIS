from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY
from train import Train
import random

class GeneratorSubmodel(AtomicDEVS):
	def __init__(self, IATMin, IATMax, aMin, aMax, vMax):
		AtomicDEVS.__init__(self, "GeneratorSubmodel")
		self.state = True

		self.outport = self.addOutPort("outport")

		self.IATMin = IATMin
		self.IATMax = IATMax
		self.aMin = aMin
		self.aMax = aMax
		self.vMax = vMax
		self.ID = 0
		self.creationTime = 0

		random.seed(1234)

	def timeAdvance(self):
	    # Random interarrival time
		newIAT = random.randint(self.IATMin, self.IATMax - 1)
		print("###", newIAT)
		self.creationTime += newIAT
		return newIAT

	def outputFnc(self):
		# Generate a train
		self.ID += 1

		newA = random.randint(self.aMin, self.aMax - 1)
		newID = self.ID
		creationTime = self.creationTime
		return {self.outport: Train(newID, newA, self.vMax, creationTime)}

	def intTransition(self):
		return True

class Queue(AtomicDEVS):
	def __init__(self):
		AtomicDEVS.__init__(self, "Queue")
		self.state = "EMPTY"#EMPTY, WAITING or SENDING

		self.inport = self.addInPort("input")

		self.qRack = self.addInPort("qRack")
		self.qSend = self.addOutPort("qSend")
		self.trainOut = self.addOutPort("trainOut")

		self.q = []

	def timeAdvance(self):
		if self.state == "EMPTY":
			return INFINITY
	  	elif self.state == "WAITING":
			return 1#send a query every 1 sec
		elif self.state == "SENDING":
			return 0
		else:
			raise DEVSException("unknown state {} in Queue timeAdvance".format(self.state))

	def outputFnc(self):
		# BEWARE: ouput is based on the OLD state
        # and is produced BEFORE making the transition.
		if self.state == "WAITING":
			return {self.qSend: "queryToEnter"}
		elif self.state == "SENDING":
			train = self.q.pop(0)
			return {self.trainOut: train}

	def intTransition(self):
		if self.state == "WAITING":
			return "WAITING"
		elif self.state == "SENDING":
			if len(self.q) > 0:
				return "WAITING"
			else:
				return "EMPTY"
		else:
			raise DEVSException("invalid state {} in Queue intTransition".format(self.state))

	def extTransition(self, inputs):
		inTrain = inputs.get(self.inport)
		inAck   = inputs.get(self.qRack)
		# if incoming train
		if inTrain is not None:
			self.q.append(inTrain)
			return "SENDING"
		# if incoming ack
		if inAck is not None:
			if inAck == "GREEN":
				return "SENDING"
			else:#RED
				return "WAITING"
		raise DEVSException("invalid state {} in Queue extTransition".format(self.state))

"""
class Generator(CoupledDEVS):
	def __init__(self, IATMin, IATMax, aMin, aMax, vMax):
		CoupledDEVS.__init__(self, "Generator")

		self.gen = self.addSubModel(GeneratorSubmodel(IATMin, IATMax, aMin, aMax, vMax))
		self.q   = self.addSubModel(Queue())

		self.connectPorts(self.gen.outport, self.q.inport)
"""
