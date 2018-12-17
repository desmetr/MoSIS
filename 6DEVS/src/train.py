from formulas import *

class Train:
	def __init__(self, ID, aMax, creationTime):
		self.ID = ID
		self.aMax = aMax

		self.v = 0	# Current speed
		self.remainingX = 0

		self.creationTime = creationTime

	def __str__(self):
		return "Train: ID = " + str(self.ID) + ", max acceleration = " + str(self.aMax) + ", departure time = " + str(self.departureTime) + ", v = " + str(self.v) + ", remaining x = " + str(self.remainingX)

	# TODO: t_poll? self.L?
	def accelerate(self):
		# accelerate as fast as possible
		# 100km/h = 27m/s
		return acceleration_formula(self.v, 27, self.remainingX, self.aMax)

	def brake(self, currentLength):
		# gradually brake
		(newV, xTravelled) = brake_formula(self.v, 1.0, self.remainingX)
		self.remainingX = currentLength - xTravelled
