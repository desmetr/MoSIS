#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw

class Adder(CBD):        
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OutDouble"])
        self.addBlock(ConstantBlock(block_name="three", value=3.0))
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        self.addBlock(AdderBlock(block_name="adder"))
      
        self.addConnection("three", "adder")
        self.addConnection("two", "adder")
        self.addConnection("adder", "OutDouble")

# adder = Adder(block_name="adder")
# adder.run(1, verbose=True)

class Double(CBD):        
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["InNumber"], output_ports=["OutDouble"])
        self.addBlock(ProductBlock(block_name="mult"))
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        
        self.addConnection("InNumber", "mult")
        self.addConnection("two", "mult")
        self.addConnection("mult", "OutDouble")

# double = Double(block_name="double")
# double.run(1, verbose=True)

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

modInv = ModInv(block_name="modInv")
modInv.run(1)