from formulas import *

class Train:
	def __init__(self, ID, aMax, departureTime, creationTime):
		self.ID = ID
		self.aMax = aMax
		self.departureTime = departureTime

		self.v = 0	# Current speed
		self.remainingX = 0	
		
		self.lifeTime = creationTime

	def __str__(self):
		return "Train: ID = " + str(self.ID) + ", max acceleration = " + str(self.aMax) + ", departure time = " + str(self.departureTime) + ", v = " + str(self.v) + ", remaining x = " + str(self.remainingX)

	def __repr__(self):
		return "Train: ID = " + str(self.ID)

	# TODO: t_poll? self.L?
	def accelerate(self, currentLength):
		# accelerate as fast as possible
		# 100km/h = 27m/s
		(v, t) = acceleration_formula(self.v, 27, currentLength - self.remainingX, self.aMax)
		self.v = v
		return t

	def brake(self, currentLength):
		# gradually brake
		(newV, xTravelled) = brake_formula(self.v, 1.0, self.remainingX)
		self.remainingX = currentLength - xTravelled

	def updateLifeTime(self, currentTime):
		self.lifeTime += currentTime