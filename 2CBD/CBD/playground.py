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
        # self.addBlock(OutputPortBlock(block_name="OutDouble", parent=None))

        self.addConnection("three", "adder")
        self.addConnection("two", "adder")
        self.addConnection("adder", "OutDouble")

class Double(CBD):        
    def __init__(self, block_name):
        CBD.__init__(self, 
                     block_name, 
                     input_ports=["InNumber"], 
                     output_ports=["OutDouble"])
        self.addBlock(ProductBlock(block_name="mult"))
        self.addBlock(ConstantBlock(block_name="two", value=2.0))
        
        self.addConnection("InNumber", "mult")
        self.addConnection("two", "mult")
        self.addConnection("mult", "OutDouble")

double = Double(block_name="double")
# double.run(1, verbose=True)

adder = Adder(block_name="adder")
adder.run(1, verbose=True)