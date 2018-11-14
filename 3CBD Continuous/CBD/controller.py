from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw
from TrainCostModelBlock import *
from bokeh.plotting import figure, output_file, show

class ComputerBlock(BaseBlock):
    """
    IN1 = current time
    OUT1 = desired speed
    """
    def __init__(self, block_name):
        BaseBlock.__init__(self, block_name, input_ports=["IN1"], output_ports=["OUT1"])
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
    ERROR = DesiredSpeed - ActualSpeed of train
    DELTA = timestep
    F_TRACTION = F_traction
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["ERROR","DELTA"], output_ports=["F_TRACTION"])
        # Constants
        self.addBlock(ConstantBlock("Zero", 0))
        self.addBlock(ConstantBlock("Kp", 200))
        self.addBlock(ConstantBlock("Ki", 0))
        self.addBlock(ConstantBlock("Kd", 0))

        self.addBlock(AdderBlock("Adder1"))
        self.addBlock(AdderBlock("Adder2"))
        self.addBlock(ProductBlock("ProductKp"))
        self.addBlock(ProductBlock("ProductKi"))
        self.addBlock(ProductBlock("ProductKd"))
        self.addBlock(IntegratorBlock("Integrator"))
        self.addBlock(DerivatorBlock("Derivator"))

        # Connections
        self.addConnection("Kp", "ProductKp")
        self.addConnection("ERROR", "ProductKp")

        self.addConnection("ERROR", "Integrator")
        self.addConnection("Zero", "Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "Integrator", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("ERROR", "Derivator")
        self.addConnection("Zero", "Derivator", input_port_name="IC")
        self.addConnection("DELTA", "Derivator", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Ki", "ProductKi")
        self.addConnection("Integrator", "ProductKi")

        self.addConnection("Kd", "ProductKd")
        self.addConnection("Derivator", "ProductKd")

        self.addConnection("ProductKd", "Adder2")
        self.addConnection("ProductKi", "Adder2")

        self.addConnection("ProductKp", "Adder1")
        self.addConnection("Adder2", "Adder1")

        self.addConnection("Adder1", "F_TRACTION", output_port_name="OUT1")

class PlantCBD(CBD):
    """
    F_TRACTION = acceleration of the train
    DELTA = timestep
    V_TRAIN = speed of the train
    X_PASSENGER = displacement of the passenger
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["F_TRACTION","DELTA"], output_ports=["V_PASSENGER", "V_TRAIN", "X_PASSENGER", "X_TRAIN"])

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
        self.addBlock(ConstantBlock("Half", 0.5))

        # Integrator Blocks
        self.addBlock(IntegratorBlock("Integrator"))
        self.addBlock(IntegratorBlock("Integrator2"))

        ##############
        # EQUATION 1 #
        ##############

        # Part1 = k * (- X_PASSENGER)
        self.addBlock(NegatorBlock("Negator1"))
        self.addBlock(ProductBlock("Product1"))

        self.addConnection("Integrator2", "Negator1", output_port_name="OUT1")
        self.addConnection("Negator1", "Product1")
        self.addConnection("K","Product1")

        # Part2 = C * (- V_PASSENGER)
        self.addBlock(NegatorBlock("Negator2"))
        self.addBlock(ProductBlock("Product2"))

        self.addConnection("Integrator", "Negator2", output_port_name="OUT1")
        self.addConnection("Negator2", "Product2")
        self.addConnection("C", "Product2")

        # Part 3 = M_Passenger * (F_TRACTION / (M_Train + M_Passenger))
        self.addBlock(AdderBlock("Adder3"))
        self.addBlock(InverterBlock("Inverter3"))
        self.addBlock(ProductBlock("Product3_1"))
        self.addBlock(ProductBlock("Product3_2"))

        self.addConnection("M_Train", "Adder3", output_port_name="OUT1")
        self.addConnection("M_Passenger", "Adder3", output_port_name="OUT1")

        self.addConnection("Adder3", "Inverter3")

        self.addConnection("Inverter3", "Product3_1")
        self.addConnection("F_TRACTION", "Product3_1", output_port_name="OUT1")

        self.addConnection("Product3_1", "Product3_2")
        self.addConnection("M_Passenger", "Product3_2", output_port_name="OUT1")

        # Part 4 = Part1 + Part2 - Part3
        self.addBlock(AdderBlock("Adder4_1"))
        self.addBlock(AdderBlock("Adder4_2"))
        self.addBlock(NegatorBlock("Negator4"))

        self.addConnection("Product1", "Adder4_1")
        self.addConnection("Product2", "Adder4_1")

        self.addConnection("Product3_2", "Negator4")

        self.addConnection("Negator4", "Adder4_2")
        self.addConnection("Adder4_1", "Adder4_2")

        # Part5 = Part4 / M_Passenger
        self.addBlock(InverterBlock("Inverter5"))
        self.addBlock(ProductBlock("Product5"))

        self.addConnection("M_Passenger", "Inverter5", output_port_name="OUT1")

        self.addConnection("Adder4_2", "Product5")
        self.addConnection("Inverter5", "Product5")

        # Integrator
        self.addConnection("Product5", "Integrator")
        self.addConnection("V0Passenger", "Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "Integrator", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator", "Integrator2", output_port_name="OUT1")
        self.addConnection("X0Passenger", "Integrator2", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "Integrator2", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator", "V_PASSENGER", output_port_name="OUT1")
        self.addConnection("Integrator2", "X_PASSENGER", output_port_name="OUT1")

        ##############
        # EQUATION 2 #
        ##############

        self.addBlock(ProductBlock("_Product1"))
        self.addBlock(ProductBlock("_Product2"))
        self.addBlock(ProductBlock("_Product3"))
        self.addBlock(ProductBlock("_Product4"))
        self.addBlock(ProductBlock("_Product5"))
        self.addBlock(ProductBlock("_Product6"))
        self.addBlock(AdderBlock("_Adder1"))
        self.addBlock(AdderBlock("_Adder2"))
        self.addBlock(NegatorBlock("_Negator"))
        self.addBlock(InverterBlock("_Inverter"))
        self.addBlock(IntegratorBlock("_Integrator"))
        self.addBlock(IntegratorBlock("_Integrator2"))

        self.addConnection("Half", "_Product1")
        self.addConnection("P", "_Product1")

        self.addConnection("_Product1", "_Product2")
        self.addConnection("CD", "_Product2")

        self.addConnection("_Product2", "_Product3")
        self.addConnection("A", "_Product3")

        self.addConnection("_Product3", "_Product4")
        self.addConnection("_Integrator", "_Product4")

        self.addConnection("_Product4", "_Product5")
        self.addConnection("_Integrator", "_Product5")

        self.addConnection("_Product5", "_Negator")

        self.addConnection("_Negator", "_Adder1")
        self.addConnection("F_TRACTION", "_Adder1")

        self.addConnection("M_Train", "_Adder2")
        self.addConnection("M_Passenger", "_Adder2")

        self.addConnection("_Adder2", "_Inverter")

        self.addConnection("_Adder1", "_Product6")
        self.addConnection("_Inverter", "_Product6")

        self.addConnection("_Product6", "_Integrator")
        self.addConnection("V0Train", "_Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "_Integrator", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("_Integrator", "_Integrator2")
        self.addConnection("X0Train", "_Integrator2", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "_Integrator2", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("_Integrator", "V_TRAIN")
        self.addConnection("_Integrator2", "X_TRAIN")

class CompleteTrainSystemCBD(CBD):
    """
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=[], output_ports=["V_IDEAL", "V_TRAIN"])
        # Blocks
        self.addBlock(TimeCBD("Time"))
        self.addBlock(ComputerBlock("Lookup"))
        self.addBlock(AdderBlock("Sum"))
        self.addBlock(NegatorBlock("Negator"))
        self.addBlock(PIDControllerCBD("PIDController"))
        self.addBlock(PlantCBD("Plant"))
        self.addBlock(CostFunctionBlock("CostFunction"))

        # Connections
        self.addConnection("Time", "Lookup")
        self.addConnection("Lookup", "Sum")
        self.addConnection("Negator", "Sum")

        self.addConnection("Sum","PIDController", input_port_name="ERROR")
        self.addConnection("Time", "PIDController", output_port_name="DELTA", input_port_name="DELTA")

        self.addConnection("PIDController", "Plant", output_port_name="F_TRACTION", input_port_name="F_TRACTION")
        self.addConnection("Time", "Plant", output_port_name="DELTA", input_port_name="DELTA")

        self.addConnection("Plant", "Negator", output_port_name="V_TRAIN")

        self.addConnection("Lookup", "CostFunction", output_port_name="OUT1", input_port_name="InVi")
        self.addConnection("Plant", "CostFunction", output_port_name="V_TRAIN", input_port_name="InVTrain")
        self.addConnection("Plant", "CostFunction", output_port_name="X_PASSENGER", input_port_name="InXPerson")
        self.addConnection("Time", "CostFunction", output_port_name="DELTA",  input_port_name="InDelta")

        self.addConnection("Lookup", "V_IDEAL", output_port_name="OUT1")
        self.addConnection("Plant", "V_TRAIN", output_port_name="V_TRAIN")

def testPlant():
    cbd = CBD("CBD")
    cbd.addBlock(PlantCBD("Plant"))
    draw(cbd, "output/testPlant.dot")
    cbd.addBlock(ConstantBlock("F_TRACTION_IN", 10))
    cbd.addBlock(ConstantBlock("DELTA_IN", 1))

    cbd.addConnection("F_TRACTION_IN", "Plant", input_port_name="F_TRACTION")
    cbd.addConnection("DELTA_IN", "Plant", input_port_name="DELTA")

    cbd.run(10)

def testTimeCBD():
    cbd = TimeCBD("testTimeCBD")

    cbd.run(10)

def testPIDControllerCBD():
    cbd = CBD("CBD")
    cbd.addBlock(PIDControllerCBD("testPIDControllerCBD"))
    cbd.addBlock(ConstantBlock("ERROR_IN", 1))
    cbd.addBlock(ConstantBlock("DELTA_IN", 1))

    cbd.addConnection("ERROR_IN", "testPIDControllerCBD", input_port_name="ERROR")
    cbd.addConnection("DELTA_IN", "testPIDControllerCBD", input_port_name="DELTA")

    cbd.run(10)

def runCBD(cbd, steps):
    cbd.run(steps)

    times = []
    outputIdeal = []

    for timeValuePair in cbd.getSignal("V_IDEAL"):
        times.append(timeValuePair.time)
        outputIdeal.append(timeValuePair.value)

    outputTrain = []

    for timeValuePair in cbd.getSignal("V_TRAIN"):
        outputTrain.append(timeValuePair.value)

    # Plot
    p = figure(title=cbd.getBlockName(), x_axis_label='time', y_axis_label='N')
    p.circle(x=times, y=outputIdeal, color="red")
    p.circle(x=times, y=outputTrain, color="blue")
    show(p)

# testPlant()
# print "PlantCBD OK"
# testTimeCBD()
# print "TimeCBD OK"
# testPIDControllerCBD()
# print "PIDControllerCBD OK"

steps = 350
completeTrainSystem = CompleteTrainSystemCBD("completeTrainSystem")
runCBD(completeTrainSystem, steps)
# draw(completeTrainSystem, "output/completeTrainSystem.dot")