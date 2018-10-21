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

	def set_stream(self, stream):
		self.stream = stream

 	def scan(self):
  		self.current_state=self.transition(self.current_state, None)

		if __trace__:
			print "\ndefault transition --> "+self.current_state

		while 1:
			# look ahead at the next character in the input stream
			next_char = self.stream.getNextChar()

			# stop if this is the end of the input stream
			if next_char == None:
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

class ScannerUseCase4(Scanner):
	def __init__(self, stream):
   		# superclass constructor
		Scanner.__init__(self, stream)

		# define alphabet
		self.alphabet = ['E', 'R', 'G', 'X', '1', '2', '3', ' ']

		# define accepting states
		self.accepting_states=["S0"]

		self.string = ""
		# self.recognized = False
		self.inComment = False

	def __str__(self):
  		return self.string

  	def transition(self, state, input):
		"""
		Encodes transitions and actions
		"""

		if state == None:
			self.T = None
			self.T2 = None
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
			elif self.inComment:
				return "S0"
			elif input in self.alphabet:
				return "S0"
			else:
				return None

		elif state == "S1":
			if input == ' ':
				return "S2"
			else:
				return None

		elif state == "S2":
			if input in ['1','2']:
				self.T = input
				return "S3"
			elif input == '3':
				return "S0"
			else:
				return None

		elif state == "S3":
			if input == '\n':
				return "S4"
			else:
				return None

		elif state == "S4":
			if input == 'G' and not self.inComment:
				return "S5"
			elif input == 'E' and not self.inComment:
				return "S8"
			elif input == '#':
				self.inComment = True
				return "S4"
			elif input == '\n':
				self.inComment = False
				return "S4"
			elif self.inComment:
				return "S4"
			elif input in self.alphabet:
				return "S4"
			else:
				return None

		elif state == "S5":
			if input == ' ':
				return "S6"
			else:
				return None

		elif state == "S6":
			if input == self.T:
				return "S7"
			elif input == '3':
				return "S4"
			else:
				return None

		elif state == "S7":
			if input == '\n':
				# self.recognized = True
				return "S0"
			else:
				return None

		elif state == "S8":
			if input == ' ':
				return "S9"
			else:
				return None

		elif state == "S9":
			if (input in ['1','2']) and (input != self.T):
				self.T2 = input
				return "S10"
			elif input == '3':
				return "S4"
			else:
				return None

		elif state == "S10":
			if input == '\n':
				return "S11"
			else:
				return None

		elif state == "S11":
			if input == 'G' and not self.inComment:
				return "S12"
			elif input in self.alphabet and not self.inComment:
				return "S11"
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
			if input == self.T:
				return "S14"
			elif input == '3':
				return "S11"
			else:
				return None

		elif state == "S14":
			if input == '\n':
				return "S15"
			else:
				return None

		elif state == "S15":
			if input == 'G' and not self.inComment:
				return "S16"
			elif input == '#':
				self.inComment = True
				return "S15"
			elif input == '\n':
				self.inComment = False
				return "S15"
			elif self.inComment:
				return "S15"
			elif input in self.alphabet:
				return "S15"
			else:
				 return None

		elif state == "S16":
			if input == ' ':
				return "S17"
			else:
				return None

		elif state == "S17":
			if input == self.T2:
				return "S18"
			elif input == '3':
				return "S15"
			else:
				return None

		elif state == "S18":
			if input == '\n':
				# self.recognized = True
				return "S0"
			else:
				return None

	def entry(self, state, input):
		if input in self.alphabet:
			self.string += input
			if input in ['1','2','2']:
				self.string += ","
