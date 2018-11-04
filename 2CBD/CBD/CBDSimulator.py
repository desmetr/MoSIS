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

        self.addBlock(DelayBlock("Delay"))
        self.addBlock(ProductBlock("Product"))
        self.addBlock(AdderBlock("Adder"))
        self.addBlock(IntegratorBlock("Integrator"))

        #DeltaT
        self.addConnection("AdderDeltaT", "DelayDeltaT", output_port_name="OUT1")
        self.addConnection("Zero", "DelayDeltaT", output_port_name="OUT1", input_port_name="IC")

        self.addConnection("DelayDeltaT", "AdderDeltaT")
        self.addConnection("DeltaT", "AdderDeltaT")

        #Loop
        self.addConnection("Product", "Integrator")
        self.addConnection("AdderDeltaT", "Integrator", output_port_name="OUT1", input_port_name="delta_t")
        self.addConnection("Zero", "Integrator", output_port_name="OUT1", input_port_name="IC")

        self.addConnection("Adder", "Delay", output_port_name="OUT1")
        self.addConnection("One", "Delay", output_port_name="OUT1", input_port_name="IC")

        self.addConnection("Five", "Product")
        self.addConnection("Delay", "Product")

        self.addConnection("Two", "Adder")
        self.addConnection("Product", "Adder")

        #output
        self.addConnection("Integrator", "OUT1", output_port_name="OUT1")



latexWrite = True
latexWriter.writeBeginDocument("CBDSimulator.tex")
latexWriter.writeTitle("Assignment 2 - Task 2 - CBD")
latexWriter.writeNewSection("Equations")
latexWriter.writeBeginArray()

cbd = CBDSimulation("CBDSimulation")
draw(cbd, "CBDSimulation.dot")
cbd.run(5)

times = []
output = []

for timeValuePair in cbd.getSignal("Adder"):
    times.append(timeValuePair.time)
    output.append(timeValuePair.value)

print output, times

#Plot
# output_file("./number_gen.html", title="Even Numbers")
p = figure(title="CBD Simulator", x_axis_label='time', y_axis_label='N')
p.circle(x=times, y=output)
show(p)

latexWriter.writeEndArray()
latexWriter.writeEndDocument()
