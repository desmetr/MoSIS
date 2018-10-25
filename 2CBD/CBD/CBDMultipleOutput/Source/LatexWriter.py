# Rafael De Smet 2018 - Assignment 2 CBD

class LatexWriter:
	def __init__(self):
		self.file = open("equations.tex", "w")

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

	def writeEquations(self, sortedGraph, depGraph, curIteration):
		self.file.write("\\[\n")
		self.file.write("\\left\\{\n")
		self.file.write("\\begin{array}{c}\n")

		# TODO write equations
		self.file.write("a_1x+b_1y+c_1z=d_1 \\\\\n") 
		self.file.write("a_2x+b_2y+c_2z=d_2 \\\\\n") 
		self.file.write("a_3x+b_3y+c_3z=d_3\n")
		
		self.file.write("\\end{array}\n")
		self.file.write("\\right.\n")
		self.file.write("\\]\n")

	def getConstant(self, in1, out=None):
		result = str(in1)
		if out != None:
			result += " = " + str(out)
		return result

	def getNegation(self, in1, out=None):
		result = "-" + str(in1)
		if out != None:
			result += " = " + str(out)
		return result

	def getInverter(self, in1, out=None):
		result = "\\frac{1}{" + str(in1) + "}"
		if out != None:
			result += " = " + str(out)
		return result

	def getAddition(self, in1, in2, out=None):
		result = str(in1) + "+" + str(in2) 
		if out != None:
			result += " = " + str(out)
		return result
	
	def getProduct(self, in1, in2, out=None):
		result = str(in1) + "*" + str(in2)
		if out != None:
			result += " = " + str(out)
		return result

	def getDivision(self, in1, in2, out=None):
		result = "\\frac{" + str(in1) + "}{" + str(in2)
		if out != None:
			result += " = " + str(out)
		return result

	def getRoot(self, in1, in2, out=None):
		result = "\\sqrt[" + str(in2) + "]{" + str(in1) + "}"
		if out != None:
			result += " = " + str(out)
		return result

	def getModulo(self, in1, in2, out=None):
		result = str(in1) + "*\\mod" + str(in2)
		if out != None:
			result += " = " + str(out)
		return result