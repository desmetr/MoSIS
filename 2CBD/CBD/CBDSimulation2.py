#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw
from CBDMultipleOutput.Source.LatexWriter import *

class Counter(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name,  input_ports=[], output_ports=["OutCount"])
        
        self.addBlock(DelayBlock(block_name="delay"))
        self.addBlock(AdderBlock(block_name="sum"))
        self.addBlock(ConstantBlock(block_name="zero", value=0.0))
        self.addBlock(ConstantBlock(block_name="one", value=1.0))
        
        self.addConnection("zero", "delay", input_port_name="IC")
        self.addConnection("delay", "OutCount")
        self.addConnection("delay", "sum")
        self.addConnection("sum", "delay", input_port_name="IN1")   # LOOP
        self.addConnection("one", "sum")

class TimesFive(CBD):        
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["InNumber"], output_ports=["OutTimesFive"])
        self.addBlock(ProductBlock(block_name="mult"))
        self.addBlock(ConstantBlock(block_name="five", value=5.0))
        
        self.addConnection("InNumber", "mult")
        self.addConnection("five", "mult")
        self.addConnection("mult", "OutTimesFive")

class CBDSimulation2(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OutMultipleFive"])
        #Blocks
        counterBlock = Counter(block_name="counter")
        draw(counterBlock, "Counter.dot") 
        self.addBlock(counterBlock)

        timesFiveBlock = TimesFive(block_name="timesFive") 
        draw(timesFiveBlock, "TimesFive.dot")
        self.addBlock(timesFiveBlock)
        
        self.addConnection("counter", "timesFive", input_port_name="InNumber", output_port_name="OutCount")
        self.addConnection("timesFive", "OutMultipleFive", output_port_name="OutTimesFive")

cbd = CBDSimulation2("CBDSimulation2")
draw(cbd, "CBDSimulation2.dot")
cbd.run(5)

times = []
output = []

for timeValuePair in cbd.getSignal("OutMultipleFive"):
    times.append(timeValuePair.time)
    output.append(timeValuePair.value)

print output, times

#Plot
p = figure(title="CBD Simulation 2", x_axis_label='time', y_axis_label='N')
p.circle(x=times, y=output)
show(p)