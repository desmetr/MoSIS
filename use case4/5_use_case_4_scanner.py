#source: http://msdl.cs.mcgill.ca/people/hv/teaching/MoSIS/assignments/requirementsChecking/scanner.py

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
            # next_char = self.stream.getNextChar()
        	# if next_char == None:
			# 	break
            char1 = self.stream.getNextChar()

            # stop if this is the end of the input stream
            if char1 == None:
                break

            char2 = self.stream.getNextChar()
            char3 = self.stream.getNextChar()
            char4 = self.stream.getNextChar()
            next_char = char1+char2+char3+char4


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
        char1 = self.stream.getNextChar()
        char2 = self.stream.getNextChar()
        char3 = self.stream.getNextChar()
        char4 = self.stream.getNextChar()
        next_char = char1+char2+char3+char4
		# next_char = self.stream.getNextChar()

		if __trace__:
			print str(self.stream)+"\n"

		# now check whether to accept consumed characters
		success = self.current_state in self.accepting_states
		if success:
			self.stream.commit()
		else:
			self.stream.rollback()
		return success

class UseCase4Scanner(Scanner):
	def __init__(self, stream):
   		# superclass constructor
		Scanner.__init__(self, stream)

		# define accepting states
		self.accepting_states=["S0"]

	def __str__(self):
  		return str(self.value)+"E"+str(self.exp)

	def transition(self, state, input):
		"""
		Encodes transitions and actions
		"""
		if state == None:
			# action
			# new state
			return "S0"

        if state == "S0":
            if input == "E 1\n":
                return "S1"
            if input == "E 2\n":
                return "S12"
            elif input == "E 3\n"
                return "S0"
            elif (input[0] in "GRX") and (input[1]==" ") and (input[2] in [123]) and (input[3] == "\n"):
                return "S0"
            else:
                return None

		elif state == "S1":
			if input  == "E 2\n':
				return "S2"
   			elif input == "G 1\n":
				return "S0"
            elif (input[0] in "EGRX") and (input[1:] ==" 3\n"):
                return "S1"
            elif input[0] == "R":
                return "S1"
			else:
				return None

		elif state == "S2":
			if input == "G 1\n":
				return "S3"
            elif (input[0] in "EGRX") and (input[1:] ==" 3\n"):
                return "S2"
            elif input[0] == "R":
                return "S2"
			else:
				return None

        elif state == "S3":
			if input == "G 2\n":
				return "S0"
            elif (input[0] in "EGRX") and (input[1:] ==" 3\n"):
                return "S3"
            elif input[0] == "R":
                return "S3"
			else:
				return None

        elif state == "S12":
			if input  == "E 1\n':
				return "S22"
   			elif input == "G 2\n":
				return "S0"
            elif (input[0] in "EGRX") and (input[1:] ==" 3\n"):
                return "S12"
            elif input[0] == "R":
                return "S12"
			else:
				return None

        elif state == "S22":
			if input == "G 2\n":
				return "S32"
            elif (input[0] in "EGRX") and (input[1:] ==" 3\n"):
                return "S22"
            elif input[0] == "R":
                return "S22"
			else:
				return None

        elif state == "S32":
			if input == "G 1\n":
				return "S0"
            elif (input[0] in "EGRX") and (input[1:] ==" 3\n"):
                return "S32"
            elif input[0] == "R":
                return "S32"
			else:
				return None

		else:
			return None

	def entry(self, state, input):
		pass
