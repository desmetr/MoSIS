#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw
from CBDMultipleOutput.Source.LatexWriter import *

class CBDSimulation(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OutCBD"])
        # TODO make CBD

latexWrite = True
latexWriter.writeBeginDocument("CBDSimulator.tex")
latexWriter.writeTitle("Assignment 2 - Task 2 - CBD")
latexWriter.writeNewSection("Equations")
latexWriter.writeBeginArray()

cbd = CBDSimulation("CBDSimulation")
draw(cbd, "CBDSimulation.dot")
cbd.run(1)

times = []
output = []

for timeValuePair in cbd.getSignal("OutEven"):
    times.append(timeValuePair.time)
    output.append(timeValuePair.value)
            
#Plot
# output_file("./number_gen.html", title="Even Numbers")
p = figure(title="CBD Simulator", x_axis_label='time', y_axis_label='N')
p.circle(x=times, y=output)
show(p)

latexWriter.writeEndArray()
latexWriter.writeEndDocument()
