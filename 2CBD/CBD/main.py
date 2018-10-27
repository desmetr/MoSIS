#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw
from CBDMultipleOutput.Source.LatexWriter import *

class Adder(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OutDouble"])
        self.addBlock(ConstantBlock(block_name="three", value=3.0))
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        self.addBlock(AdderBlock(block_name="adder"))

        self.addConnection("three", "adder")
        self.addConnection("two", "adder")
        self.addConnection("adder", "OutDouble")

# TODO fix input ports bug
class Double(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["InNumber"], output_ports=["OutDouble"])
        self.addBlock(ProductBlock(block_name="mult"))
        self.addBlock(ConstantBlock(block_name="two", value=2.0))

        self.addConnection("InNumber", "mult")
        self.addConnection("two", "mult")
        self.addConnection("mult", "OutDouble")

class ModInv(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OutModInv"])
        self.addBlock(ConstantBlock(block_name="five", value=5.0))
        self.addBlock(ConstantBlock(block_name="ten", value=10.0))
        self.addBlock(ModuloBlock(block_name="modulo"))
        self.addBlock(InverterBlock(block_name="inverter"))

        self.addConnection("five", "modulo")
        self.addConnection("ten", "modulo")
        self.addConnection("modulo", "inverter")
        self.addConnection("inverter", "OutModInv")

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

class NonLinearLoopCBD1(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=[])
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        self.addBlock(ConstantBlock(block_name="three", value=3.0))
        self.addBlock(ProductBlock(block_name="product"))
        self.addBlock(RootBlock(block_name="root"))

        self.addConnection("two", "product")
        self.addConnection("three", "root")
        self.addConnection("product", "root")
        self.addConnection("root", "product") # LOOP

class ConstantCBD(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=[])
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        self.addBlock(ConstantBlock(block_name="minus1", value=-1.0))
        self.addBlock(ConstantBlock(block_name="five", value=5.0))

latexWriter.writeBeginDocument()
latexWriter.writeTitle("Assignment 2 - CBD")
latexWriter.writeNewSection("Equations")
latexWriter.writeBeginArray()

# adder = Adder(block_name="adder")
# adder.run(1)

# double = Double(block_name="double")
# double.run(1)

# modInv = ModInv(block_name="modInv")
# modInv.run(1)

# linearLoopCBD1 = LinearLoopCBD1("linearLoopCBD1")
# linearLoopCBD1.run(1)
# draw(linearLoopCBD1, "linearLoopCBD1.dot")

# linearLoopCBD2 = LinearLoopCBD2("linearLoopCBD2")
# linearLoopCBD2.run(1)
# draw(linearLoopCBD2, "linearLoopCBD2.dot")

nonLinearLoopCBD1 = NonLinearLoopCBD1("nonLinearLoopCBD1")
nonLinearLoopCBD1.run(1)
# draw(nonLinearLoopCBD1, "nonLinearLoopCBD1.dot")

# constantCBD = ConstantCBD("constantCBD")
# constantCBD.run(1)

latexWriter.writeEndArray()
latexWriter.writeEndDocument()
