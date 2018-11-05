#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw
from CBDMultipleOutput.Source.LatexWriter import *

class CBDSimulation(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])
        #Blocks
        self.addBlock(ConstantBlock("Zero",0))
        self.addBlock(ConstantBlock("One",1))
        self.addBlock(ConstantBlock("Two", 2))
        self.addBlock(ConstantBlock("Five", 5))
        self.addBlock(ConstantBlock("DeltaT", 1))

        self.addBlock(DelayBlock("DelayDeltaT"))
        self.addBlock(AdderBlock("AdderDeltaT"))

        self.addBlock(ProductBlock("Product"))
        self.addBlock(AdderBlock("Adder"))
        self.addBlock(IntegratorBlock("Integrator"))

        #DeltaT
        self.addConnection("AdderDeltaT", "DelayDeltaT", output_port_name="OUT1")
        self.addConnection("Zero", "DelayDeltaT", output_port_name="OUT1", input_port_name="IC")

        self.addConnection("DelayDeltaT", "AdderDeltaT")
        self.addConnection("DeltaT", "AdderDeltaT")

        #Loop
        self.addConnection("Five", "Product")
        self.addConnection("AdderDeltaT", "Product", output_port_name="OUT1")

        self.addConnection("Two", "Adder")
        self.addConnection("Product", "Adder")

        self.addConnection("Product", "Integrator")
        self.addConnection("AdderDeltaT", "Integrator", output_port_name="OUT1", input_port_name="delta_t")
        self.addConnection("Zero", "Integrator", output_port_name="OUT1", input_port_name="IC")

        #output
        self.addConnection("Integrator", "OUT1", output_port_name="OUT1")

cbd = CBDSimulation("CBDSimulation1")
draw(cbd, "CBDSimulation1.dot")
cbd.run(5)

times = []
output = []

for timeValuePair in cbd.getSignal():
    times.append(timeValuePair.time)
    output.append(timeValuePair.value)

print output, times

#Plot
p = figure(title="CBD Simulation 1", x_axis_label='time', y_axis_label='N')
p.circle(x=times, y=output)
show(p)