from pypdevs.DEVS import *
from pypdevs.infinity import INFINITY

class RailwaySegment(AtomicDEVS):
    segmentCounter = 0

    def __init__(self, L):
        RailwaySegment.segmentCounter += 1
        AtomicDEVS.__init__(self, "RailwaySegment{}".format(RailwaySegment.segmentCounter))

        self.L = L

        self.state = "EMPTY"#EMPTY, ACCELERATING, QUERYING, BRAKING, LEAVING, RESPONDING
        self.previousState = None #needed to get out of RESPONDING state
        self.currentTrain = None

        self.qRecv = self.addInPort("qRecv")
        self.qSack = self.addOutPort("qSack")
        self.trainIn = self.addInPort("trainIn")

        self.qSend = self.addOutPort("qSend")
        self.qRack = self.addInPort("qRack")
        self.trainOut = self.addOutPort("trainOut")

    def timeAdvance(self):
        if self.state in ["EMPTY", "QUERYING"]:
            return INFINITY
        elif self.state == "ACCELERATING":
            time = self.currentTrain.accelerate()
            return time
        elif self.state == "BRAKING":
            tPoll = 1#send query every 1 sec
            self.currentTrain.brake(tPoll)
            return tPoll
        elif self.state == "LEAVING":
            time = self.currentTrain.accelerate(leaving=True)
            return time
        elif self.state == "RESPONDING":
            return 0
        else:
            raise DEVSException("invalid state {} in RailwaySegment timeAdvance".format(self.state))

    def outputFnc(self):
        # BEWARE: ouput is based on the OLD state
        # and is produced BEFORE making the transition.
        if self.state == "RESPONDING":
            if self.previousState == "EMPTY":
                return {self.qSack: "GREEN"}
            else:
                return {self.qSack: "RED"}
        elif self.state in ["ACCELERATING", "BRAKING"]:
            return {self.qSend: "queryToEnter"}
        elif self.state in "LEAVING":
            return {self.trainOut: self.currentTrain}

    def intTransition(self):
        if self.state == "RESPONDING":
            temp =  self.previousState
            self.previousState = None
            return temp
        elif self.state in ["ACCELERATING","BRAKING"]:
            return "QUERYING"
        elif self.state == "LEAVING":
            return "EMPTY"
        else:
            raise DEVSException("invalid state {} in RailwaySegment intTransition".format(self.state))

    def extTransition(self, inputs):
        inQRecv = inputs.get(self.qRecv)
        trainIn = inputs.get(self.trainIn)
        inQRack = inputs.get(self.qRack)

        #if incoming query
        if inQRecv is not None:
            self.previousState = self.state
            return "RESPONDING"
        #if incoming train
        if trainIn is not None:
            self.currentTrain = trainIn
            self.currentTrain.resetXRemaining(self.L)
            return "ACCELERATING"
        #if incoming ACK
        if inQRack is not None:
            if inQRack == "GREEN":
                return "LEAVING"
            else:#RED
                return "BRAKING"
