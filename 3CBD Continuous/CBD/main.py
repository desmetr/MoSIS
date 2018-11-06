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