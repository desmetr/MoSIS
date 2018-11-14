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
        #blocks
        self.addBlock(NegatorBlock("Negator"))
        self.addBlock(IntegratorBlock("Integrator1"))
        self.addBlock(IntegratorBlock("Integrator2"))
        draw(IntegratorBlock("integral"), "output/Integrator.dot")

        self.addBlock(ConstantBlock("X0", 0))
        self.addBlock(ConstantBlock("V0", 1))
        self.addBlock(ConstantBlock("DeltaT", 0.001))

        #connections
        self.addConnection("Negator", "Integrator1")
        self.addConnection("V0", "Integrator1", input_port_name="IC")
        self.addConnection("DeltaT", "Integrator1", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator1", "Integrator2")
        self.addConnection("X0", "Integrator2", input_port_name="IC")
        self.addConnection("DeltaT", "Integrator2", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator2", "Negator", output_port_name="OUT1")

        self.addConnection("Integrator2", "OUT1", output_port_name="OUT1")


# Harmonic oscillator using derivative blocks
class CBD_B(CBD):
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])
        #blocks
        self.addBlock(NegatorBlock("Negator"))
        self.addBlock(DerivatorBlock("Derivator1"))
        self.addBlock(DerivatorBlock("Derivator2"))
        draw(DerivatorBlock("derivative"), "output/Derivator.dot")

        self.addBlock(ConstantBlock("V0", 1))
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("DeltaT", 0.001))


        # Connections
        self.addConnection("Negator", "Derivator1")
        self.addConnection("V0", "Derivator1", input_port_name="IC")
        self.addConnection("DeltaT", "Derivator1", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Derivator1", "Derivator2")
        self.addConnection("Zero", "Derivator2", input_port_name="IC")
        self.addConnection("DeltaT", "Derivator2", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Derivator2", "Negator", output_port_name="OUT1")

        self.addConnection("Derivator2", "OUT1", output_port_name="OUT1")


class ErrorCBD_A(CBD):
    def __init__(self, block_name, step_size=1):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])
        self.addBlock(ClockCBD("Clock"))
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("DeltaT", step_size))
        self.addConnection("Zero", "Clock", input_port_name="IC")
        self.addConnection("DeltaT", "Clock", input_port_name="delta_t")

        self.addBlock(SinBlock("Sin"))
        self.addBlock(GenericBlock(block_name="Absolute", block_operator="fabs"))
        self.addBlock(AdderBlock("Adder"))
        self.addBlock(NegatorBlock("Negator"))

        self.addBlock(CBD_A("CBD_A"))
        self.addBlock(IntegratorBlock("Integrator"))

        # Connections
        self.addConnection("Clock", "Integrator", output_port_name="OUT1", input_port_name="delta_t")
        self.addConnection("Zero", "Integrator", input_port_name="IC")

        self.addConnection("CBD_A", "Negator")
        self.addConnection("Negator", "Adder")
        self.addConnection("Sin", "Adder")
        self.addConnection("Adder", "Absolute")
        self.addConnection("Absolute", "Integrator")

        self.addConnection("Integrator", "OUT1", output_port_name="OUT1")

class ErrorCBD_B(CBD):
    def __init__(self, block_name, step_size=1):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1"])
        self.addBlock(ClockCBD("Clock"))
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("DeltaT", step_size))
        self.addConnection("Zero", "Clock", input_port_name="IC")
        self.addConnection("DeltaT", "Clock", input_port_name="delta_t")

        self.addBlock(SinBlock("Sin"))
        self.addBlock(GenericBlock(block_name="Absolute", block_operator="fabs"))
        self.addBlock(AdderBlock("Adder"))
        self.addBlock(NegatorBlock("Negator"))

        self.addBlock(CBD_B("CBD_B"))
        self.addBlock(IntegratorBlock("Integrator"))

        # Connections
        self.addConnection("Clock", "Integrator", output_port_name="OUT1", input_port_name="delta_t")
        self.addConnection("Zero", "Integrator", input_port_name="IC")

        self.addConnection("CBD_B", "Negator")
        self.addConnection("Negator", "Adder")
        self.addConnection("Sin", "Adder")
        self.addConnection("Adder", "Absolute")
        self.addConnection("Absolute", "Integrator")

        self.addConnection("Integrator", "OUT1", output_port_name="OUT1")

def runCBD(cbd, steps):
    cbd.run(steps)

    times = []
    output = []

    for timeValuePair in cbd.getSignal():
        times.append(timeValuePair.time)
        output.append(timeValuePair.value)

    # print output, times

    # Plot
    p = figure(title=cbd.getBlockName(), x_axis_label='time', y_axis_label='N')
    p.circle(x=times, y=output)
    show(p)

A = CBD_A("CBD_A")
# draw(A, "output/CBD_A.dot")
# runCBD(A, 6000)

B = CBD_B("CBD_B")
# draw(B, "output/CBD_B.dot")
# runCBD(B, 6000)

# ErrorA1 = ErrorCBD_A("ErrorA1", step_size=0.1)
# ErrorA2 = ErrorCBD_A("ErrorA2", step_size=0.001)  # VEEL BETERE ERROR PLOT
# draw(ErrorA1, "output/ErrorA.dot")
# runCBD(ErrorA1, 1000)
# runCBD(ErrorA2, 1000)

# ErrorB1 = ErrorCBD_B("ErrorB1", step_size=0.1)
# ErrorB2 = ErrorCBD_B("ErrorB2", step_size=0.001)
# draw(ErrorB1, "output/ErrorB.dot")
# runCBD(ErrorB1, 1000)
# runCBD(ErrorB2, 1000)
