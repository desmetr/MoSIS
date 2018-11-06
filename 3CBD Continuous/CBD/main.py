#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw

# Harmonic oscillator using integral blocks
class CBD_A(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("One", 1))
        self.addBlock(ConstantBlock("DeltaT", 1))
        self.addBlock(NegatorBlock("Negate")) 
        self.addBlock(IntegratorBlock("Integrator1"))    # Handles highest order
        self.addBlock(IntegratorBlock("Integrator2"))    # Handles lowest order

        self.addBlock(DelayBlock("DelayDeltaT"))
        self.addBlock(AdderBlock("AdderDeltaT"))

        # DeltaT 
        self.addConnection("AdderDeltaT", "DelayDeltaT")
        self.addConnection("Zero", "DelayDeltaT", input_port_name="IC")

        self.addConnection("DelayDeltaT", "AdderDeltaT")
        self.addConnection("DeltaT", "AdderDeltaT")

        # Oscillator
        self.addConnection("One", "Integrator1", input_port_name="IC")
        self.addConnection("Zero", "Integrator2", input_port_name="IC")
        self.addConnection("AdderDeltaT", "Integrator1", input_port_name="delta_t")
        self.addConnection("AdderDeltaT", "Integrator2", input_port_name="delta_t")
        self.addConnection("Integrator1", "Integrator2")    # x'
        self.addConnection("Integrator2", "Negate")         # -x
        self.addConnection("Negate", "Integrator1")
        self.addConnection("Integrator2", "OUT1", output_port_name="OUT1")    

# Harmonic oscillator using derivative blocks
class CBD_B(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])


        
cbdA = CBD_A("CBD_A")
draw(cbdA, "CBD_A.dot")
cbdA.run(10)

times = []
output = []

for timeValuePair in cbdA.getSignal():
    times.append(timeValuePair.time)
    output.append(timeValuePair.value)

print output, times

#Plot
# p = figure(title="Harmonic Oscillator 1", x_axis_label='time', y_axis_label='N')
# p.circle(x=times, y=output)
# show(p)