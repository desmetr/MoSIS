from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw
from TrainCostModelBlock import *
from bokeh.plotting import figure, output_file, show

steps = 350

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
    ERROR = TODO
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
        self.addConnection("Ki", "ProductKi")
        self.addConnection("Kd", "ProductKd")

        self.addConnection("ProductKp", "Adder1")
        self.addConnection("Adder2", "Adder1")

        self.addConnection("ProductKd", "Adder2")
        self.addConnection("ProductKi", "Adder2")

        # Integrator
        self.addConnection("ERROR", "Integrator")
        self.addConnection("Zero", "Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "Integrator", output_port_name="OUT1", input_port_name="delta_t")
        self.addConnection("Integrator", "ProductKi")

        # Derivator
        self.addConnection("ERROR", "Derivator")
        self.addConnection("Zero", "Derivator", input_port_name="IC")
        self.addConnection("DELTA", "Derivator", output_port_name="OUT1", input_port_name="delta_t")
        self.addConnection("Derivator", "ProductKd")

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
        # self.addBlock(ConstantBlock("X0Train", 0))
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

        # Integrator Blocks
        self.addBlock(IntegratorBlock("Integrator"))
        self.addBlock(IntegratorBlock("Integrator2"))

        ##############
        # EQUATION 1 #
        ##############

        # Part1 = k * (- X_PASSENGER)
        self.addBlock(ProductBlock("Product1"))
        self.addBlock(NegatorBlock("Negator1"))

        self.addConnection("Integrator2", "Negator1", output_port_name="OUT1")
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
        self.addBlock(ProductBlock("Product3_1"))
        self.addBlock(ProductBlock("Product3_2"))

        self.addConnection("M_Train", "Adder3")
        self.addConnection("M_Passenger", "Adder3", output_port_name="OUT1")

        self.addConnection("Adder3", "Inverter3")

        self.addConnection("Inverter3", "Product3_1")
        self.addConnection("F_TRACTION", "Product3_1")

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
        self.addConnection("X0Passenger", "Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "Integrator", output_port_name="OUT1", input_port_name="delta_t")

        self.addConnection("Integrator", "V_PASSENGER", output_port_name="OUT1")    #?? juist?

        ##############
        # EQUATION 2 #
        ##############

        # Part1 = 1/2 * P * V*V * CD * A
        self.addBlock(ConstantBlock("_Half", 0.5))
        self.addBlock(ProductBlock("_Product1_1"))
        self.addBlock(ProductBlock("_Product1_2"))
        self.addBlock(ProductBlock("_Product1_3"))
        self.addBlock(ProductBlock("_Product1_4"))
        self.addBlock(ProductBlock("_Product1_5"))

        self.addBlock(IntegratorBlock("_Integrator"))
        # self.addBlock(IntegratorBlock("_Integrator2"))

        # Connections
        self.addConnection("_Half", "_Product1_1")
        self.addConnection("P", "_Product1_1")

        self.addConnection("_Product1_1", "_Product1_2")
        self.addConnection("_Integrator", "_Product1_2", output_port_name="OUT1")

        self.addConnection("_Product1_2", "_Product1_3")
        self.addConnection("_Integrator", "_Product1_3", output_port_name="OUT1")

        self.addConnection("_Product1_3", "_Product1_4")
        self.addConnection("CD", "_Product1_4")

        self.addConnection("_Product1_4", "_Product1_5")
        self.addConnection("A", "_Product1_5")

        # Part2 = F_traction - Part1
        self.addBlock(NegatorBlock("_Negator2"))
        self.addBlock(AdderBlock("_Adder2"))

        # Connections
        self.addConnection("_Product1_5", "_Negator2")

        self.addConnection("_Negator2", "_Adder2")
        self.addConnection("F_TRACTION", "_Adder2")

        # Part3 = Part2 / (M_Train + M_Passenger)
        self.addBlock(ProductBlock("_Product3"))

        # Connections
        self.addConnection("_Adder2", "_Product3")
        self.addConnection("Inverter3", "_Product3", output_port_name="OUT1")

        # Integrator
        self.addConnection("_Product3", "_Integrator")
        self.addConnection("V0Train", "_Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "_Integrator", output_port_name="OUT1", input_port_name="delta_t")

        # self.addConnection("_Integrator", "_Integrator2")
        # self.addConnection("X0Train", "_Integrator2", output_port_name="OUT1", input_port_name="IC")
        # self.addConnection("DELTA", "_Integrator2", output_port_name="OUT1", input_port_name="delta_t")

        # Outputs
        # self.addConnection("Integrator", "X_PASSENGER", output_port_name="OUT1")
        self.addConnection("_Integrator", "V_TRAIN", output_port_name="OUT1")

        ##############
        # EQUATION 3 #
        ##############

        self.addBlock(IntegratorBlock("__Integrator"))
        
        # Connections
        self.addConnection("V_PASSENGER", "__Integrator")
        self.addConnection("V0Passenger", "__Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "_Integrator", output_port_name="OUT1", input_port_name="delta_t")
        
        # Outputs
        self.addConnection("__Integrator", "X_PASSENGER", output_port_name="OUT1")

        ##############
        # EQUATION 4 #
        ##############

        self.addBlock(IntegratorBlock("___Integrator"))
        
        # Connections
        self.addConnection("V_TRAIN", "___Integrator")
        self.addConnection("V0Train", "___Integrator", output_port_name="OUT1", input_port_name="IC")
        self.addConnection("DELTA", "_Integrator", output_port_name="OUT1", input_port_name="delta_t")
        
        # Outputs
        self.addConnection("___Integrator", "X_TRAIN", output_port_name="OUT1")

class CostFunctionCBD(CBD):
    """
    V_IDEAL =
    V_TRAIN =
    X_PASSENGER =
    DELTA = timestep
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, input_ports=["IN_V_I","IN_V_TRAIN","DELTA","IN_X_PASSENGER"], output_ports=["OUT1"])
        
        self.addBlock(CostFunctionBlock("CostFunction"))

        self.addConnection("IN_V_I", "CostFunction", input_port_name="InVi")
        self.addConnection("IN_V_TRAIN", "CostFunction", input_port_name="InVTrain")
        self.addConnection("DELTA", "CostFunction", input_port_name="InDelta")
        self.addConnection("IN_X_PASSENGER", "CostFunction", input_port_name="InXPerson")

        self.addConnection("CostFunction", "OUT1")

class CompleteTrainSystemCBD(CBD):
    """
    """
    def __init__(self, block_name):
        CBD.__init__(self, block_name, 
                     input_ports=["ERROR", "V_IDEAL"], 
                     output_ports=["DELTA", "F_TRACTION", "OUT_DELTA", "V_TRAIN", "OUT1", "X_PASSENGER"])
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

        self.addConnection("Sum","PIDController", input_port_name="ERROR")
        self.addConnection("Time", "PIDController", output_port_name="DELTA", input_port_name="DELTA")

        self.addConnection("PIDController", "Plant", output_port_name="F_TRACTION", input_port_name="F_TRACTION")
        self.addConnection("Time", "Plant", output_port_name="DELTA", input_port_name="DELTA")

        self.addConnection("Plant", "Negator", output_port_name="V_TRAIN")

        self.addConnection("Lookup", "CostFunction", output_port_name="OUT1", input_port_name="IN_V_I")
        self.addConnection("Plant", "CostFunction", output_port_name="V_TRAIN", input_port_name="IN_V_TRAIN")
        self.addConnection("Plant", "CostFunction", output_port_name="X_PASSENGER", input_port_name="IN_X_PASSENGER")
        self.addConnection("Time", "CostFunction", output_port_name="DELTA",  input_port_name="DELTA")

completeTrainSystem = CompleteTrainSystemCBD("completeTrainSystem")
completeTrainSystem.run(steps)
# draw(completeTrainSystem, "output/completeTrainSystem.dot")

# tempTimeCBD = TimeCBD("tempTimeCBD")
# draw(tempTimeCBD, "output/TimeCBD.dot")

# tempPIDControllerCBD = PIDControllerCBD("tempPIDControllerCBD")
# draw(tempPIDControllerCBD, "output/PIDControllerCBD.dot")

# tempPlantCBD = PlantCBD("tempPlantCBD")
# draw(tempPlantCBD, "output/PlantCBD.dot")

# tempCostFunctionCBD = CostFunctionCBD("tempCostFunctionCBD")
# draw(tempCostFunctionCBD, "output/CostFunctionCBD.dot")

# times = []
# output = []

# for timeValuePair in cbd.getSignal("IN_V_I"):
#     times.append(timeValuePair.time)
#     output.append(timeValuePair.value)

# for i in [0, 10, 160, 200, 260]:
#     print output[i], times[i]

# # Plot
# p = figure(title=ideal.getBlockName(), x_axis_label='time', y_axis_label='N')
# p.circle(x=times, y=output, color="red")
# show(p)