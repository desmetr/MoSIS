#
# scanner.py
#
# COMP 304B Assignment 3
#
import string

False = 0
True  = 1

# trace FSA dynamics (True | False)
__trace__ = False 
# __trace__ = True 

class Scanner:
	"""
	A simple Finite State Automaton simulator.
	Used for scanning an input stream.
	"""
	def __init__(self, stream):
		self.set_stream(stream)
		self.current_state=None
		self.accepting_states=[]

		self.current_char = None

	def set_stream(self, stream):
		self.stream = stream

 	def scan(self):
  		self.current_state=self.transition(self.current_state, None)

		if __trace__:
			print "\ndefault transition --> "+self.current_state

		while 1:
			# look ahead at the next character in the input stream
			next_char = self.stream.getNextChar()
			self.current_char = next_char

			# stop if this is the end of the input stream
			if next_char == None:
				self.current_state = "S15"
				break

			if __trace__:
				print str(self.stream)
				if self.current_state != None:
					print "transition "+self.current_state+" -| "+next_char,
   
		    # perform transition and its action to the appropriate new state 
			next_state = self.transition(self.current_state, next_char)

			if __trace__:
				if next_state == None: 
					print
				else:
					print "|-> "+next_state
    
			# stop if a transition was not possible
			if next_state == None:
				break
			else:
				self.current_state = next_state
				# perform the new state's entry action (if any)
				self.entry(self.current_state, next_char)

		# now, actually consume the next character in the input stream 
		next_char = self.stream.getNextChar()

		if __trace__:
			print str(self.stream)+"\n"

		# now check whether to accept consumed characters
		success = self.current_state in self.accepting_states
		if success:
			self.stream.commit()
		else:
			self.stream.rollback()
		return success

class UseCase3Scanner(Scanner):
	def __init__(self, stream):
   		# superclass constructor
		Scanner.__init__(self, stream)

		# define alphabet
		self.alphabet = ['E', 'R', 'G', 'X', '1', '2', '3', ' ']

		# define accepting states
		self.accepting_states=["S14", "S15"]

	def __str__(self):
  		return self.result

  	def transition(self, state, input):
		"""
		Encodes transitions and actions
		"""
		if state == None:
			self.result = ""
			self.seenR1 = False
			self.seenR2 = False
			self.inComment = False
			self.recognized = False
			return "S0"

		elif state == "S0":
			if input == 'E' and not self.inComment:
				return "S1"
			elif input == '#':
				self.inComment = True
				return "S0"
			elif input == '\n':
				self.inComment = False
				return "S0"
			elif input != 'E' and self.inComment:
				return "S0"
			else:
				return None

		elif state == "S1":
			if input == ' ':
				return "S2"
			else:
				return None

		elif state == "S2":
			if input == '3':
				return "S3"
			else:
				return None

		elif state == "S3":
			if input == 'R' and not self.inComment:
				return "S4"
			elif input == 'X' and not self.inComment:
				if self.seenR1 or self.seenR2:
					return "S3"
				else:
					return None
			elif input == '#':
				self.inComment = True
				return "S3"
			elif input == '\n':
				self.inComment = False
				return "S3"
			elif self.inComment:
				return "S3"
			else:
				return None

		elif state == "S4":
			if input == ' ':
				return "S5"
			else:
				return None

		elif state == "S5":
			if input == '1':
				self.seenR1 = True
				return "S6"
			elif input == '2':
				self.seenR2 = True
				return "S7"
			else:
				return None

		elif state == "S6":
			if input == 'R' and not self.inComment:
				self.seenR1 = False
				return "S8"
			elif input == '#':
				self.inComment = True
				return "S6"
			elif input == '\n':
				self.inComment = False
				return "S6"
			elif self.inComment:
				return "S6"
			elif input in self.alphabet:
				return "S6"
			else:
				return None

		elif state == "S7":
			if input == 'R' and not self.inComment:
				self.seenR2 = False
				return "S8"
			elif input == '#':
				self.inComment = True
				return "S7"
			elif input == '\n':
				self.inComment = False
				return "S7"
			elif self.inComment:
				return "S7"
			elif input in self.alphabet:
				return "S7"
			else:
				return None

		elif state == "S8":
			if input == ' ':
				return "S9"
			else:
				return None

		elif state == "S9":
			if input == '1':
				self.seenR1 = True
				return "S10"
			elif input == '2':
				self.seenR2 = True
				return "S11"
			else:
				return None

		elif state == "S10":
			if input == 'X' and not self.inComment:
				self.seenR1 = False
				return "S12"
			elif input == '#':
				self.inComment = True
				return "S10"
			elif input == '\n':
				self.inComment = False
				return "S10"
			elif self.inComment:
				return "S10"
			elif input in self.alphabet:
				return "S10"
			else:
				return None

		elif state == "S11":
			if input == 'X' and not self.inComment:
				self.seenR2 = False
				return "S12"
			elif input == '#':
				self.inComment = True
				return "S11"
			elif input == '\n':
				self.inComment = False
				return "S11"
			elif self.inComment:
				return "S11"
			elif input in self.alphabet:
				return "S11"
			else:
				return None

		elif state == "S12":
			if input == ' ':
				return "S13"
			else:
				return None

		elif state == "S13":
			if input == '3':
				return "S14"
			else:
				return None

		# If we have recognized one statement, keep checking if there is more input
		elif state == "S14":
			if input != None:
				self.recognized = True
				return "S0"
			else:
				return None

		else:
			return None

	def entry(self, state, input):
		if input in self.alphabet and not self.inComment:
			self.result += input
		if input in ['1', '2', '3']:
			self.result += " "

		if self.recognized:
			print ">> recognized " + self.__str__()
			print ">> continuing reading input"
			self.result = ""
			self.seenR1 = False
			self.seenR2 = False
			self.inComment = False
			self.recognized = False