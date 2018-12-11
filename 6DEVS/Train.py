class Train:
	def __init__(self, ID, a_max, departure_time):
		self.ID = ID
		self.a_max = a_max
		self.departure_time = departure_time

		self.v = 0	# Current speed
		self.remaining_x = 0	
		
	def __str__(self):
		return "Train: ID = " + str(self.ID) + ", a_max = " + str(self.a_max) + ", departure_time = " + str(self.departure_time) + ", v = " + str(self.v) + ", remaining_x = " + str(self.remaining_x)
