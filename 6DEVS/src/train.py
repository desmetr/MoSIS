import formulas

class Train:
	def __init__(self, ID, aMax, vMax, creationTime):
		self.ID = ID
		self.aMax = aMax
		self.vMax = vMax

		self.v = 0	# Current speed
		self.xRemaining = 0

		self.creationTime = creationTime

	def __str__(self):
		return "Train: ID = " + str(self.ID) + ", max acceleration = " + str(self.aMax) + ", departure time = " + str(self.departureTime) + ", v = " + str(self.v) + ", remaining x = " + str(self.remainingX)

	def accelerate(self, leaving=False):
        # acceleration_formula(v_0, v_max, x_remaining, a)
		distance = -1
		if leaving:
			distance = xRemaining
		else:
			distance = xRemaining - 1000
		self.v, time = formulas.acceleration_formula(self.v, self.vMax, self.xRemaining, self.aMax)
		return time

	def brake(self, t_poll):
		# brake_formula(v_0, t_poll, x_remaining)
		self.v, xTravelled = formulas.brake_formula(self.v, t_poll, self.xRemaining)
		self.xRemaining -= xTravelled

	def resetXRemaining(x):
		self.xRemaining = x
