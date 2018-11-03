# Rafael De Smet 2018 - Assignment 2 CBD

class LatexWriter:
	def __init__(self):
		self.file = open("equations.tex", "w")

		self.constantCounter = 0
		self.variableCounter = 0

	def writeBeginDocument(self):
		self.file.write("\\documentclass[a4paper,12pt]{article}\n")
		self.file.write("\\usepackage{amsmath}\n")
		self.file.write("\\begin{document}\n")

	def writeEndDocument(self):
		self.file.write("\\end{document}")

	def writeTitle(self, title):
		self.file.write("\\title{" + title + "}")
		self.file.write("\\date{}")	# No date.
		self.file.write("\\maketitle\n")

	def writeNewSection(self, name):
		self.file.write("\\section{" + name + "}\n")

	def writeBeginArray(self):
		self.file.write("\\[\n")
		self.file.write("\\left\\{\n")
		self.file.write("\\begin{array}{c}\n")
		
	def writeEndArray(self):
		self.file.write("\\end{array}\n")
		self.file.write("\\right.\n")
		self.file.write("\\]\n")

	def writeConstant(self, in1):
		result = "c_" + str(self.constantCounter) + " = " + str(in1)
		self.file.write(result + " \\\\\n")
		self.constantCounter += 1

	def writeNegation(self, in1):
		result = "v_" + str(self.variableCounter) + " = " + str(in1)
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1

	def writeInvertion(self, in1):
		result = "v_" + str(self.variableCounter) + " = " + "\\frac{1}{" + str(in1) + "}"
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1

	def writeAddition(self, in1, in2):
		result = "v_" + str(self.variableCounter) + " = " + str(in1) + "+" + str(in2) 
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1
	
	def writeProduct(self, in1, in2):
		result = "v_" + str(self.variableCounter) + " = " + str(in1) + "*" + str(in2)
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1

	def writeGeneric(self, in1, operator):
		result = "v_" + str(self.variableCounter) + " = "
		if operator == "exp":
			result += "e^{" + str(in1) + "}"
		elif operator == "ceil":
			result += "\\left \\lceil{" + str(in1) + "}\\right \\rceil"
		elif operator == "floor":
			result += "\\left \\lfloor{" + str(in1) + "}\\right \\rfloor"
		elif operator == "fabs":
			result += "\\lvert" + str(in1) + "\\rvert"
		else:
			result += operator + "(" + str(in1) + ")"
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1
	
	def writeDivision(self, in1, in2):
		result = "v_" + str(self.variableCounter) + " = " + "\\frac{" + str(in1) + "}{" + str(in2) + "}"
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1

	def writeRoot(self, in1, in2):
		result = "v_" + str(self.variableCounter) + " = " + "\\sqrt[" + str(in2) + "]{" + str(in1) + "}"
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1

	def writeModulo(self, in1, in2, out=None):
		result = "v_" + str(self.variableCounter) + " = " + str(in1) + "*\\mod" + str(in2)
		self.file.write(result + " \\\\\n")
		self.variableCounter += 1

	def writeSystemOfEquations(self, solverInput):
		M1 = solverInput[0]
		M2 = solverInput[1]

		result = ""
		for row in range(len(M1)):
			for column in range(len(M1[row])):
				result += str(M1[row][column]) + "x_" + str(column)

				if column == len(M2) - 1:
					result += " = " + str(M2[row]) + " \\\\\n"
				else:
					result += "+"

		newResult = []
		resultList = list(result)
		found = False
		for i in range(len(resultList) - 1):
			if resultList[i] == '+' and resultList[i+1] == '-':
				newResult.append('-')
				found = True
			elif resultList[i] == '-' and found:
				found = False
			else:
				newResult.append(resultList[i])
		result = ''.join(newResult)

		self.file.write(result)

	def writeSolution(self, solutionVector):
		result = ""
		for i, solution in enumerate(solutionVector):
			result += "x_" + str(i) + " = " + str(solution) + " \\\\\n"
		self.file.write(result)

latexWriter = LatexWriter()