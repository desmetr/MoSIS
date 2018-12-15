from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY
from Train import *
import random
import Queue

class GeneratorState:
	def __init__(self):
		self.current_time = 0.0

class Generator(AtomicDEVS):
	def __init__(self, IATMin, IATMax, aMin, aMax):
		AtomicDEVS.__init__(self, "Generator")
		self.state = "outputTrain"

		self.IATMin = IATMin
		self.IATMax = IATMax
		self.aMin = aMin
		self.aMax = aMax
		
		self.qRack = self.addInPort("qRack")
		self.qSend = self.addOutPort("qSend")
		self.trainOut = self.addOutPort("trainOut")

		self.numberOfTrainsOutput = 0

		self.queue = Queue.Queue()

		self.currentTime = 0
	
	def intTransition(self):
		pass

	def timeAdvance(self):
		print "in timeAdvance"
		# TODO: correcte timeAdvance?
		self.currentTime += 1
		return 1.0

	def outputFnc(self):
		# TODO: checken of trein op track mag, zo ja pak uit queue, anders zet in queue
		print "in outputFnc"
		newIAT = random.randint(self.IATMin, self.IATMax - 1)
		newA = random.randint(self.aMin, self.aMax - 1)
		newID = self.numberOfTrainsOutput + 1
		creationTime = self.currentTime # TODO moet er nog iets bij?
		self.numberOfTrainsOutput = newID
		
		newTrain = Train(newID, newA, newIAT, creationTime)
		print "midden"
		state = self.state
		if state == "putInQueue":
			print "in putInQueue"
			self.queue.put(newTrain)
		elif state == "outputTrain":
			print "in outputTrain"
			trainToOutput = self.queue.get()
			if trainToOutput == None:
				trainToOutput = newTrain
			print "eerste return"
			return {self.trainOut: trainToOutput}
		return {self.qSend: Query("queryToEnter")}

	def extTransition(self):
		print "in extTransition"
		queryAck = inputs[self.qRack]
		if queryAck == "red":
			return "putInQueue"
		elif queryAck == "green":
			return "outputTrain"