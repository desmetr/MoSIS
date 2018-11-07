#!/usr/bin/env python
from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw

class ClockCBD(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["IC","delta_t"], output_ports=["OUT1"])
        self.addBlock(NegatorBlock("Negator"))
        self.addBlock(DelayBlock("Delay"))
        self.addBlock(AdderBlock("Adder"))

        self.addConnection("IC", "Negator")
        self.addConnection("Negator", "Delay", input_port_name="IC")
        self.addConnection("Adder", "Delay", output_port_name="OUT1", input_port_name="IN1")

        self.addConnection("Delay", "Adder")
        self.addConnection("delta_t", "Adder")

        self.addConnection("Adder", "OUT1", output_port_name="OUT1")


# Harmonic oscillator using integral blocks
class CBD_A(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])
        # Clock
        self.addBlock(ClockCBD("Clock"))
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("DeltaT", 0.00001))
        self.addConnection("Zero", "Clock", input_port_name="IC")
        self.addConnection("DeltaT", "Clock", input_port_name="delta_t")

        #blocks
        self.addBlock(NegatorBlock("Negator"))
        self.addBlock(IntegratorBlock("Integrator1"))
        self.addBlock(IntegratorBlock("Integrator2"))

        self.addBlock(ConstantBlock("X0", 0))
        self.addBlock(ConstantBlock("V0", 1))

        #connections
        self.addConnection("Negator", "Integrator1")
        self.addConnection("V0", "Integrator1", input_port_name="IC")
        self.addConnection("Clock", "Integrator1", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator1", "Integrator2")
        self.addConnection("X0", "Integrator2", input_port_name="IC")
        self.addConnection("Clock", "Integrator2", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator2", "Negator", output_port_name="OUT1")

        self.addConnection("Integrator2", "OUT1", output_port_name="OUT1")


# Harmonic oscillator using derivative blocks
class CBD_B(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])
        # Clock
        self.addBlock(ClockCBD("Clock"))
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("DeltaT", 1))
        self.addConnection("Zero", "Clock", input_port_name="IC")
        self.addConnection("DeltaT", "Clock", input_port_name="delta_t")

        #blocks
        self.addBlock(NegatorBlock("Negator"))
        self.addBlock(DerivatorBlock("Derivator1"))
        self.addBlock(DerivatorBlock("Derivator2"))

        self.addBlock(ConstantBlock("X0", 0))
        self.addBlock(ConstantBlock("V0", 1))
        self.addBlock(NegatorBlock("NegatorX0"))#overbodig als X0 = 0

        #connections
        self.addConnection("X0", "NegatorX0")

        self.addConnection("Negator", "Derivator1")
        self.addConnection("V0", "Derivator1", input_port_name="IC")
        self.addConnection("Clock", "Derivator1", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Derivator1", "Derivator2")
        self.addConnection("NegatorX0", "Derivator2", input_port_name="IC")
        self.addConnection("Clock", "Derivator2", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Derivator2", "Negator", output_port_name="OUT1")

        self.addConnection("Derivator2", "OUT1", output_port_name="OUT1")


A = CBD_A("CBD_A")
draw(A, "CBD_A.dot")
B = CBD_B("CBD_B")
draw(B, "CBD_B.dot")
A.run(1000)
# B.run(10)

times = []
output = []

for timeValuePair in A.getSignal():
    times.append(timeValuePair.time)
    output.append(timeValuePair.value)

# print output, times

# for timeValuePair in B.getSignal():
#     times.append(timeValuePair.time)
#     output.append(timeValuePair.value)
#
# print output, times


# Plot
p = figure(title="Harmonic Oscillator 1", x_axis_label='time', y_axis_label='N')
p.circle(x=times, y=output)
show(p)
