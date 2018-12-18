from pypdevs.DEVS import *
from pypdevs.simulator import Simulator

from generator import Generator, Queue
from railwaySegment import RailwaySegment
from collector import Collector

from itertools import chain

class TrainTrafficSystem(CoupledDEVS):
	def __init__(self, segmentsCount, totalLength):
		assert(segmentsCount > 0), "TrainTrafficSystem invalid segmentsCount: {}".format(segmentsCount)
		assert(totalLength > 0), "TrainTrafficSystem invalid total length: {}".format(totalLength)
		self.segmentsCount = segmentsCount
		self.totalLength = totalLength
		self.L = float(totalLength) / float(segmentsCount)

		CoupledDEVS.__init__(self, "system")

        # Submodels
		generator = self.addSubModel(Generator(IATMin=1, IATMax=2, aMin=1, aMax=10, vMax=400/3.6))
		queue = self.addSubModel(Queue())
		segments = [self.addSubModel(RailwaySegment(self.L)) for x in range(self.segmentsCount)]
		self.collector = self.addSubModel(Collector())

        # Connections
		self.connectPorts(generator.outport, queue.inport)
		l1 = [queue] + segments
		l2 = segments + [self.collector]
		for current, nxt in zip(l1, l2):
			# connect current to nxt
			self.connectPorts(current.qSend, nxt.qRecv)
			self.connectPorts(nxt.qSack, current.qRack)
			self.connectPorts(current.trainOut, nxt.trainIn)

	def getCost(self):
        # Average cost
		return 10 * self.segmentsCount + self.getPerformance()

	def getPerformance(self):
		# Average travel time
		l = self.collector.getTrains()
		return sum(train.getPerformance() for train in l) / len(l)

"""
### Experiment
system = TrainTrafficSystem(20, 10000)
sim = Simulator(system)
# sim.setVerbose()
sim.setTerminationTime(10000)
sim.setClassicDEVS()
sim.simulate()

print system.getPerformance()
# print system.collector.data()
"""
totalLengths = [5000]#, 10000, 15000, 20000]
segmentsCount = [5, 10, 15, 20, 25, 30]
terminationTime = 1000

for length in totalLengths:
	data = []
	fileName = "../data/results{}.dat".format(length)

	for segCnt in segmentsCount:
		system = TrainTrafficSystem(segCnt, length)
		sim = Simulator(system)
		sim.setTerminationTime(terminationTime)
		sim.setClassicDEVS()
		sim.simulate()

		c = system.getCost()
		p = system.getPerformance()
		data.append((segCnt, c, p))

	with open(fileName,"w") as f:
		f.write("segmentsCount, cost, performance\n")
		for d in data:
			f.write("{} {} {}\n".format(d[0], d[1], d[2]))
