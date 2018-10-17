#
# scanner.py
#
# COMP 304B Assignment 3
#
import string

False = 0
True  = 1

# trace FSA dynamics (True | False)
# __trace__ = False
__trace__ = True

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

## An example scanner, see http://moncs.cs.mcgill.ca/people/hv/teaching/SoftwareDesign/COMP304B2003/assignments/assignment3/solution/
class NumberScanner(Scanner):
	def __init__(self, stream):
   		# superclass constructor
		Scanner.__init__(self, stream)

		# define accepting states
		self.accepting_states=["S2","S4","S7"]

	def __str__(self):
  		return str(self.value)+"E"+str(self.exp)

	def transition(self, state, input):
		"""
		Encodes transitions and actions
		"""
		if state == None:
			# action
			# initialize variables
			self.value = 0
			self.exp = 0
			# new state
			return "S1"

		elif state == "S1":
			if input  == '.':
				# action
				self.scale = 0.1
				# new state
				return "S3"
   			elif '0' <= input <= '9':
				# action
				self.value = ord(string.lower(input))-ord('0')
				# new state
				return "S2"
			else:
				return None

		elif state == "S2":
			if input  == '.':
				# action
				self.scale = 0.1
				# new state
				return "S4"
			elif '0' <= input <= '9':
				# action
				self.value = self.value*10+ord(string.lower(input))-ord('0')
				# new state
				return "S2"
			elif string.lower(input)  == 'e':
				# action
				self.exp = 1
				# new state
				return "S5"
			else:
				return None

		elif state == "S3":
			if '0' <= input <= '9':
				# action
				self.value += self.scale*(ord(string.lower(input))-ord('0'))
				self.scale /= 10
				# new state
				return "S4"
			else:
				return None

		elif state == "S4":
			if '0' <= input <= '9':
				# action
				self.value += self.scale*(ord(string.lower(input))-ord('0'))
				self.scale /= 10
				# new state
				return "S4"
			elif string.lower(input)  == 'e':
				# action
				self.exp = 1
				# new state
				return "S5"
			else:
				return None

		elif state == "S5":
			if input  == '+':
				# new state
				return "S6"
			elif input == '-':
				# action
				self.exp = -1
				# new state
				return "S6"
			elif '0' <= input <= '9':
				# action
				self.exp *= ord(string.lower(input))-ord('0')
				# new state
				return "S7"
			else:
				return None

		elif state == "S6":
			if '0' <= input <= '9':
				# action
				self.exp *= ord(string.lower(input))-ord('0')
				# new state
				return "S7"
			else:
				return None

		elif state == "S7":
			if '0' <= input <= '9':
				# action
				self.exp = self.exp*10+ord(string.lower(input))-ord('0')
				# new state
				return "S7"
			else:
				return None

		else:
			return None

	def entry(self, state, input):
		pass

## An example scanner, see http://msdl.cs.mcgill.ca/people/hv/teaching/SoftwareDesign/COMP304B2003/assignments/assignment3/solution/
class CellRefScanner(Scanner):
	def __init__(self, stream):
		# superclass constructor
		Scanner.__init__(self, stream)

		# define accepting states
		self.accepting_states=["S6","S7","S8", "S9"]

	def __str__(self):
		rowstr = str(self.row)
		columnstr = str(self.column)
		if self.rowIsAbsolute:
			rowabsstr = "ABS_"
		else:
			rowabsstr = "REL_"
		if self.columnIsAbsolute:
			colabsstr = "ABS_"
		else:
			colabsstr = "REL_"
		return "("+colabsstr+columnstr+","+rowabsstr+rowstr+")"

	def transition(self, state, input):
		"""
		Encodes transitions and actions
		"""
		if state == None:
			# action
			# initialize variables
			self.row = 0
			self.rowIsAbsolute = False
			self.column = 0
			self.columnIsAbsolute = False
			# new state
			return "S1"

		elif state == "S1":
			if input == "$":
				# action
				self.columnIsAbsolute = True
				# new state
				return "S2"
			elif input.isalpha():
				return "S3"
			else:
				return None

		elif state == "S2":
			if input.isalpha():
				return "S3"
			else:
				return None

		elif state == "S3":
			if '1' <= input <= '9':
				return "S6"
			elif input == "$":
				return "S5"
			elif input.isalpha():
				self.column = 26*self.column + ord(string.lower(input)) - ord('a') + 1
				return "S4"
			else:
				return None

		elif state == "S4":
			if input == "$":
				return "S5"
			elif '1' <= input <= '9':
				return "S6"
			else:
				return None

		elif state == "S5":
			if '1' <= input <= '9':
				return "S6"
			else:
				return None

		elif state == "S6":
			if '0' <= input <= '9':
				self.row = self.row*10 + ord(string.lower(input)) - ord('0')
				return "S7"
			else:
				return None

		elif state == "S7":
			if '0' <= input <= '9':
				self.row = self.row*10 + ord(string.lower(input)) - ord('0')
				return "S8"
			else:
				return None

		elif state == "S8":
			if '0' <= input <= '9':
				self.row = self.row*10 + ord(string.lower(input)) - ord('0')
				return "S9"
			else:
				return None

		elif state == "S9":
			return None

	def entry(self, state, input):
		if state == "S3":
			self.column = ord(string.lower(input)) - ord('a') + 1
		elif state == "S5":
			self.rowIsAbsolute = True
		elif state == "S6":
			self.row = ord(string.lower(input)) - ord('0')


class UseCase3Scanner(Scanner):
	def __init__(self, stream):
   		# superclass constructor
		Scanner.__init__(self, stream)

		# define alphabet
		self.alphabet = ['E', 'R', 'G', 'X', '1', '2', '3', ' ']

		# define accepting states
		self.accepting_states=["S14"]

	def __str__(self):
  		return self.string

  	def transition(self, state, input):
		"""
		Encodes transitions and actions
		"""

		if state == None:
			self.string = ""
			self.seenR1 = False
			self.seenR2 = False
			return "S0"

		elif state == "S0":
			if input == 'E':
				return "S1"
			elif input != 'E':
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
			if input == 'R':
				return "S4"
			elif input == 'X':
				if self.seenR1 or self.seenR2:
					return "S3"
				else:
					return None
			elif input != 'R':
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
			if input == 'R':
				self.seenR1 = False
				return "S8"
			elif input != 'R' and input != 'X':
				return "S6"
			else:
				return None

		elif state == "S7":
			if input == 'R':
				self.seenR2 = False
				return "S8"
			elif input != 'R' and input != 'X':
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
			if input == 'X':
				self.seenR1 = False
				return "S12"
			elif input != 'R' and input != 'X':
				return "S10"
			else:
				return None

		elif state == "S11":
			if input == 'X':
				self.seenR2 = False
				return "S12"
			elif input != 'R' and input != 'X':
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

		else:
			return None

	def entry(self, state, input):
		if input in self.alphabet:
			self.string += input

class UseCase4Scanner(Scanner):
	def __init__(self, stream):
   		# superclass constructor
		Scanner.__init__(self, stream)

		# define alphabet
		self.alphabet = ['E', 'R', 'G', 'X', '1', '2', '3', ' ']

		# define accepting states
		self.accepting_states=["S0"]

	def __str__(self):
  		return self.string

  	def transition(self, state, input):
		"""
		Encodes transitions and actions
		"""

		if state == None:
			self.string = ""
			self.T = None
			self.T2 = None
			return "S0"

		elif state == "S0":
			if input == 'E':
				return "S1"
			else:
				return "S0"

		elif state == "S1":
			if input == ' ':
				return "S2"
			else:
				return None

		elif state == "S2":
			if input == '3':
				return "S0"
			elif (input == '1') or (input == '2'):
				self.T = input
				return "S3"
			else:
				return None

		elif state == "S3":
			if input == '\n':
				return "S4"
			else:
				return None

		elif state == "S4":
			if input == 'G':
				return "S5"
			elif input == 'E':
				return "S8"
			else:
				return "S4"

		elif state == "S5":
			if input == ' ':
				return "S6"
			else:
				return None

		elif state == "S6":
			if input == self.T:
				return "S7"
			elif input == 'E':
				return "S19"
			else:
				return None

		elif state == "S7":
			if input == '\n':
				return "S0"
			else:
				return None

		elif state == "S8":
			if input == ' ''':
				return "S9"
			else:
				return None

		elif state == "S9":
			if (input in ['1','2']) and (input != self.T):
				self.T2 = input
				return "S10"
			else:
				return None

		elif state == "S10":
			if input == '\n':
				return "S11"
			else:
				return None

		elif state == "S11":
			if input == 'G':
				return "S12"
			else:
				return "S11"

		elif state == "S12":
			if input == ' ':
				return "S13"
			else:
				return None

		elif state == "S13":
			if input == self.T:
				return "S14"
			elif input == '3':
				return "S20"
			else:
				return None

		elif state == "S14":
			if input == '\n':
				return "15"
			else:
				return None

		elif state == "S15":
			if input == 'G':
				return "S16"
			else:
				return "S15"

		elif state == "S16":
			if input == ' ':
				return "S17"
			else:
				return None

		elif state == "S17":
			if input == self.T2:
				return "S18"
			elif input == '3':
				return "S21"
			else:
				return None

		elif state == "S18":
			if input == '\n':
				return "S0"
			else:
				return None

		elif state == "S19":
			if input == '\n':
				return "S4"
			else:
				return None

		elif state == "S20":
			if input == '\n':
				return "S11"
			else:
				return None

		elif state == "S21":
			if input == '\n':
				return "S15"
			else:
				return None

	def entry(self, state, input):
		if input in self.alphabet:
			self.string += input
