numberOfQueries = 0
numberOfQueryAcks = 0

class Query:
	def __init__(self, query):
		global numberOfQueries
		self.ID = numberOfQueries
		self.query = query

		numberOfQueries += 1

	def __str__(self):
		return "Query: ID = " + str(self.ID) + ", query = " + str(self.query)
		
class QueryAck:
	def __init__(self, light):
		global numberOfQueryAcks
		self.ID = numberOfQueryAcks
		self.light = light
		
		numberOfQueryAcks += 1

	def __str__(self):
		return "QueryAck: ID = " + str(self.ID) + ", light = " + str(self.light)