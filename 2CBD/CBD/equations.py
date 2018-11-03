#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw
from CBDMultipleOutput.Source.LatexWriter import *

class LinearLoopCBD1(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=[])
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        self.addBlock(ConstantBlock(block_name="minus1", value=-1.0))
        self.addBlock(ConstantBlock(block_name="five", value=5.0))
        self.addBlock(ProductBlock(block_name="product"))
        self.addBlock(AdderBlock(block_name="adder1"))
        self.addBlock(NegatorBlock(block_name="negator"))
        self.addBlock(AdderBlock(block_name="adder2"))

        self.addConnection("two", "product")
        self.addConnection("minus1", "adder1")
        self.addConnection("five", "adder2")
        self.addConnection("product", "adder1")
        self.addConnection("adder1", "negator")
        self.addConnection("negator", "adder2")
        self.addConnection("adder2", "product") # LOOP

class LinearLoopCBD2(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=[])
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        self.addBlock(ConstantBlock(block_name="three", value=3.0))
        self.addBlock(ProductBlock(block_name="product"))
        self.addBlock(AdderBlock(block_name="adder"))
        self.addBlock(NegatorBlock(block_name="negator"))

        self.addConnection("two", "product")
        self.addConnection("three", "negator")
        self.addConnection("negator", "adder")
        self.addConnection("product", "adder")
        self.addConnection("adder", "product") # LOOP

latexWrite = True
latexWriter.writeBeginDocument("equations.tex")
latexWriter.writeTitle("Assignment 2 - Task 3 - CBD")
latexWriter.writeNewSection("Equations")
latexWriter.writeBeginArray()

linearLoopCBD1 = LinearLoopCBD1("linearLoopCBD1")
linearLoopCBD1.run(1)
draw(linearLoopCBD1, "linearLoopCBD1.dot")

# Uncomment this if you want to execute this CBD.
# linearLoopCBD2 = LinearLoopCBD2("linearLoopCBD2")
# linearLoopCBD2.run(1)
# draw(linearLoopCBD2, "linearLoopCBD2.dot")

latexWriter.writeEndArray()
latexWriter.writeEndDocument()
