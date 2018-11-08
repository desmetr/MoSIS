from CBDMultipleOutput.Source.CBD import *
from LatexWriter import *

class ComputerBlock(BaseBlock):
    """
    IN1 = current time
    OUT1 = desired speed
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["IN1"], output_ports=["OUT1"])
        #IN1 = current time
        #OUT1 = the current value

    def	compute(self, curIteration):
        # TO IMPLEMENT
        in1 = self.getInputSignal(curIteration, "IN1").value

        result = -1
        if in1 < 10:
            result = 0
        elif in1 < 160:
            result = 10
        elif in1 < 200:
            result = 4
        elif in1 < 260:
            result = 14
        else:
            result = 6
        self.appendToSignal(result, "OUT1")

        if LatexWriter.latexWrite:
            latexWriter.writeAddition(in1)

class TimeCBD(CBD):
    """
    OUT1 = current time
    DELTA = timestep
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["OUT1", "DELTA"])
        # Blocks
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("H", 1))
        self.addBlock(AdderBlock("Sum"))
        self.addBlock(DelayBlock("Delay"))

        # Connections
        self.addConnection("Sum", "Delay")
        self.addConnection("Zero", "Delay", input_port_name="IC")

        self.addConnection("Delay", "Sum")
        self.addConnection("H", "Sum")

        self.addConnection("Delay", "OUT1", output_port_name="OUT1")
        self.addConnection("H", "DELTA", output_port_name="OUT1")

class PIDControllerCBD(CBD):
    """
    IN1 = TODO
    DELTA = timestep
    F_TRACTION = F_traction
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["IN1","DELTA"], output_ports=["F_TRACTION"])
        # TODO
        pass

class PlantCBD(CBD):
    """
    F_TRACTION = acceleration of the train
    DELTA = timestep
    V_TRAIN = speed of the train
    X_PERSON = displacement of the passenger
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["F_TRACTION","DELTA"], output_ports=["V_TRAIN", "X_PERSON"])

        # Constants for Integrators
        self.addBlock(ConstantBlock("X0Train", 0))
        self.addBlock(ConstantBlock("V0Train", 0))
        self.addBlock(ConstantBlock("X0Passenger", 0))
        self.addBlock(ConstantBlock("V0Passenger", 0))

        # Constant Blocks
        self.addBlock(ConstantBlock("M_Passenger", 73))
        self.addBlock(ConstantBlock("M_Train", 6000))
        self.addBlock(ConstantBlock("K", 300))
        self.addBlock(ConstantBlock("C", 150))
        self.addBlock(ConstantBlock("CD", 0.6))
        self.addBlock(ConstantBlock("P", 1.2))
        self.addBlock(ConstantBlock("A", 9.12))

        #############
        # EQUATION1 #
        #############

        # Part1 = k * (- X_PASSENGER)
        self.addBlock(ProductBlock("Product1"))
        self.addBlock(NegatorBlock("Negator1"))

        self.addConnection("Integrator2", "Negator1", out_port_name="OUT1")
        self.addConnection("Negator1", "Product1")
        self.addConnection("K","Product1")

        # Part2 = C * (- V_PASSENGER)
        self.addBlock(ProductBlock("Product2"))
        self.addBlock(NegatorBlock("Negator2"))

        self.addConnection("Integrator", "Negator2", output_port_name="OUT1")
        self.addConnection("Negator2", "Product2")
        self.addConnection("C", "Product2")

        # Part 3 = M_Passenger * (F_TRACTION / (M_Train + M_Passenger))
        self.addBlock(AdderBlock("Adder3"))
        self.addBlock(InverterBlock("Inverter3"))
        self.addBlock(ProductBlock("Product3.1"))
        self.addBlock(ProductBlock("Product3.2"))

        self.addConnection("M_Train", "Adder3")
        self.addConnection("M_Passenger", "Adder3", outport_port_name="OUT1")

        self.addConnection("Adder3", "Inverter3")

        self.addConnection("Inverter3", "Product3.1")
        self.addConnection("F_TRACTION", "Product3.1")

        self.addConnection("Product3.1", "Product3.2")
        self.addConnection("M_Passenger", "Product3.2", outport_port_name="OUT1")

        # Part 4 = Part1 + Part2 - Part3
        self.addBlock(AdderBlock("Adder4.1"))
        self.addBlock(AdderBlock("Adder4.2"))
        self.addBlock(InverterBlock("Negator4"))

        self.addConnection("Product1", "Adder4.1")
        self.addConnection("Product2", "Adder4.1")

        self.addConnection("Product3.2", "Negator4")

        self.addConnection("Negator4", "Adder4.2")
        self.addConnection("Adder4.1", "Adder4.2")

        # Part5 = Part4 / M_Passenger
        self.addBlock(InverterBlock("Inverter5"))
        self.addBlock(ProductBlock("Product5"))

        self.addConnection("M_Passenger", "Inverter5", output_port_name="OUT1")

        self.addConnection("Adder4.2", "Product5")
        self.addConnection("Inverter5", "Product5")

        # Integrator
        self.addBlock(Integrator("Integrator"))
        self.addBlock(Integrator("Integrator2"))

        self.addConnection("Product5", "Integrator")
        self.addConnection("V0Passenger", "Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "Integrator", out_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator", "Integrator2", output_port_name="OUT1")
        self.addConnection("X0Passenger", "Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "Integrator", out_port_name="OUT1", input_port_name="delta_t")

        #############
        # EQUATION2 #
        #############

        # Part1 = 1/2 * P * V*V * CD * A
        self.addBlock(ConstantBlock("_Half", 0.5))
        self.addBlock(ProductBlock("_Product1.1"))
        self.addBlock(ProductBlock("_Product1.2"))
        self.addBlock(ProductBlock("_Product1.3"))
        self.addBlock(ProductBlock("_Product1.4"))
        self.addBlock(ProductBlock("_Product1.5"))

        self.addConnection("_Half", "_Product1.1")
        self.addConnection("P", "_Product1.1")

        self.addConnection("_Product1.1", "_Product1.2")
        self.addConnection("_Integrator", "_Product1.2", output_port_name="OUT1")

        self.addConnection("_Product1.2", "_Product1.3")
        self.addConnection("_Integrator", "_Product1.3", output_port_name="OUT1")

        self.addConnection("_Product1.3", "_Product1.4")
        self.addConnection("CD", "_Product1.3")

        self.addConnection("_Product1.4", "_Product1.5")
        self.addConnection("A", "_Product1.5")

        # Part2 = F_traction - Part1
        self.addBlock(NegatorBlock("_Negator2"))
        self.addBlock(AdderBlock("_Adder2"))

        self.addConnection("_Product1.5", "_Negator2")

        self.addConnection("_Negator2", "_Adder2")
        self.addConnection("F_TRACTION", "_Adder2")

        # Part3 = Part2 / (M_Train + M_Passenger)
        self.addBlock(ProductBlock("_Product3"))

        self.addConnection("_Adder2", "_Product3")
        self.addConnection("Inverter3", "_Product3", output_port_name="OUT1")

        # Integrator
        self.addBlock(IntegratorBlock("_Integrator"))
        self.addBlock(IntegratorBlock("_Integrator2"))

        self.addConnection("_Product3", "_Integrator")
        self.addConnection("V0Train", "_Integrator", out_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "_Integrator", out_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("_Integrator", "_Integrator2")
        self.addConnection("X0Train", "_Integrator2", out_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "_Integrator2", out_port_name="OUT1", input_port_name="delta_t")

        # Outputs
        self.addConnection("Integrator", "X_PERSON", out_port_name="OUT1)
        self.addConnection("_Integrator2", "V_TRAIN", out_port_name="OUT1)

class CostFunctionCBD(CBD):
    """
    V_IDEAL =
    V_TRAIN =
    X_PERSON =
    DELTA = timestep
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["V_IDEAL","V_TRAIN","X_PERSON","DELTA"], output_ports=[])
        # TODO
        pass

class CompleteTrainSystemCBD(CBD):
    """
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=[])
        # Blocks
        self.addBlock(TimeCBD("Time"))
        self.addBlock(ComputerBlock("Lookup"))
        self.addBlock(AdderBlock("Sum"))
        self.addBlock(NegatorBlock("Negator"))
        self.addBlock(PIDControllerCBD("PIDController"))
        self.addBlock(PlantCBD("Plant"))
        self.addBlock(CostFunctionCBD("CostFunction"))

        # Connections
        self.addConnection("Time", "Lookup")

        self.addConnection("Lookup", "Sum")
        self.addConnection("Negator", "Sum")

        self.addConnection("Sum","PIDController")
        self.addConnection("Time", "PIDController", output_port_name="DELTA", input_port_name="DELTA")

        self.addConnection("PIDController", "Plant", output_port_name="F_TRACTION", input_port_name="F_TRACTION")
        self.addConnection("Time", "Plant", out_port_name="OUT_DELTA", input_port_name="DELTA")

        self.addConnection("Plant", "Negator", output_port_name="V_TRAIN")

        self.addConnection("Lookup", "CostFunction", output_port_name="OUT1", input_port_name="V_IDEAL")
        self.addConnection("Plant", "CostFunction", out_port_name="V_TRAIN", input_port_name="V_TRAIN")
        self.addConnection("Plant", "CostFunction", output_port_name="X_PERSON", input_port_name="X_PERSON")
        self.addConnection("Time", "CostFunction", out_port_name="DELTA",  input_port_name="DELTA")
