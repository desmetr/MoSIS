# Rafael De Smet 2018 - Assignment 2 CBD

class LatexWriter:
	def __init__(self):
		self.file = open("equations.tex", "w")

	def writeBeginDocument(self):
		self.file.write("\\documentclass[a4paper,12pt]{article}\n")
		self.file.write("\\begin{document}\n")

	def writeEndDocument(self):
		self.file.write("\\end{document}")

	def writeTitle(self, title):
		self.file.write("\\title{" + title + "}")
		self.file.write("\\date{}")	# No date.
		self.file.write("\\maketitle\n")

	def writeNewSection(self, name):
		self.file.write("\\section{" + name + "}\n")

	def writeEquations(self):
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