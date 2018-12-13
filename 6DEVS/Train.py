class Train:
	def __init__(self, ID, aMax, departureTime, creationTime):
		self.ID = ID
		self.aMax = aMax
		self.departureTime = departureTime

		self.v = 0	# Current speed
		self.remainingX = 0	
		
		self.creationTime = creationTime

	def __str__(self):
		return "Train: ID = " + str(self.ID) + ", max acceleration = " + str(self.aMax) + ", departure time = " + str(self.departureTime) + ", v = " + str(self.v) + ", remaining x = " + str(self.remainingX)
