import formulas

class Train:
	def __init__(self, ID, aMax, vMax, creationTime):
		self.ID = ID
		self.aMax = aMax
		self.vMax = vMax

		self.v = 0	# Current speed
		self.xRemaining = 0

		self.creationTime  = creationTime
		self.departureTime = -1
		self.arrivalTime   = -1

	def __str__(self):
		return "Train: ID = " + str(self.ID) + ", max acceleration = " + str(self.aMax)+"  v = " + str(self.v) + ", remaining x = " + str(self.xRemaining)

	def __repr__(self):
		return "Train: ID = {}".format(self.ID)

	def accelerate(self, leaving=False):
        # acceleration_formula(v_0, v_max, x_remaining, a)
		distance = -1
		time = -1
		if leaving:
			distance = self.xRemaining
			self.v, time = formulas.acceleration_formula(self.v, self.vMax, self.xRemaining, self.aMax)
			self.xRemaining = 0
		else:
			distance = self.xRemaining - 1000
			self.v, time = formulas.acceleration_formula(self.v, self.vMax, self.xRemaining, self.aMax)
			self.xRemaining = 1000
		return int(time)

	def brake(self, t_poll):
		# brake_formula(v_0, t_poll, x_remaining)
		self.v, xTravelled = formulas.brake_formula(self.v, t_poll, self.xRemaining)
		self.xRemaining -= xTravelled

	def resetXRemaining(self, x):
		self.xRemaining = x

	def setDepartureTime(self, t):
		self.departureTime = t

	def setArrivalTime(self, t):
		self.setArrivalTime = t
