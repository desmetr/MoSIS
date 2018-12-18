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

        self.timeToLeave = None
        self.timeTo1000  = None

        self.qRecv = self.addInPort("qRecv")
        self.qSack = self.addOutPort("qSack")
        self.trainIn = self.addInPort("trainIn")

        self.qSend = self.addOutPort("qSend")
        self.qRack = self.addInPort("qRack")
        self.trainOut = self.addOutPort("trainOut")

    def timeAdvance(self):
        # print self.time_last[0], self.elapsed, self.time_last, self.time_next
        if self.state in ["EMPTY", "QUERYING"]:
            return INFINITY
        elif self.state == "ACCELERATING":
            if self.L < 1000:#we can immediatly see the light so don't accelerate
                return 0
            else:
                if self.timeTo1000 is None:
                    timeAdv = self.currentTrain.accelerate()
                    currentTime = self.time_last[0]
                    self.timeTo1000 = (timeAdv, currentTime)
                    print "## timeTo1000", self.timeTo1000
                    return timeAdv
                else:
                    print "## timeTo1000", self.timeTo1000
                    return self.timeTo1000[0]
        elif self.state == "BRAKING":
            tPoll = 1#send query every 1 sec
            self.currentTrain.brake(tPoll)
            return tPoll
        elif self.state == "LEAVING":
            if self.timeToLeave is None:
                timeAdv = self.currentTrain.accelerate(leaving=True)
                currentTime = self.time_last[0]
                self.timeToLeave = (timeAdv, currentTime)
                print "## timeToLeave", self.timeToLeave
                return timeAdv
            else:
                print "## timeToLeave", self.timeToLeave
                return self.timeToLeave[0]
        elif self.state == "RESPONDING":
            return 0
        else:
            raise DEVSException("invalid state {} in RailwaySegment timeAdvance".format(self.state))

    def outputFnc(self):
        # BEWARE: ouput is based on the OLD state
        # and is produced BEFORE making the transition.
        if self.state == "RESPONDING":
            if self.previousState == "EMPTY":
                print "<-- {}".format("GREEN")
                return {self.qSack: "GREEN"}
            else:
                if self.previousState == "LEAVING":
                    timeAdvOld, previousTime = self.timeToLeave
                    currentTime = self.time_last[0]
                    timeAdv = timeAdvOld - (currentTime - previousTime)
                    self.timeToLeave = (timeAdv, currentTime)
                elif self.previousState == "ACCELERATING":
                    timeAdvOld, previousTime = self.timeTo1000
                    currentTime = self.time_last[0]
                    timeAdv = timeAdvOld - (currentTime - previousTime)
                    self.timeTo1000 = (timeAdv, currentTime)
                # print "timeAdvNew {}, currentTime {}, timeAdvOld {}, previousTime {}".format(timeAdv, currentTime, timeAdvOld, previousTime)
                print "<-- {}".format("RED")
                return {self.qSack: "RED"}
        elif self.state in ["ACCELERATING", "BRAKING"]:
            print "==> {}".format("queryToEnter")
            return {self.qSend: "queryToEnter"}
        elif self.state in "LEAVING":
            print "==> {}".format(self.currentTrain)
            return {self.trainOut: self.currentTrain}

    def intTransition(self):
        if self.state == "RESPONDING":
            temp =  self.previousState
            self.previousState = None
            return temp
        elif self.state == "ACCELERATING":
            self.timeTo1000 = None
            return "QUERYING"
        elif self.state == "BRAKING":
            return "QUERYING"
        elif self.state == "LEAVING":
            self.timeToLeave = None
            return "EMPTY"
        else:
            raise DEVSException("invalid state {} in RailwaySegment intTransition".format(self.state))

    def extTransition(self, inputs):
        inQRecv = inputs.get(self.qRecv)
        trainIn = inputs.get(self.trainIn)
        inQRack = inputs.get(self.qRack)

        #if incoming query
        if inQRecv is not None:
            print "--> {}".format(inQRecv)
            self.previousState = self.state
            return "RESPONDING"
        #if incoming train
        if trainIn is not None:
            print "--> {}".format(trainIn)
            self.currentTrain = trainIn
            self.currentTrain.resetXRemaining(self.L)
            return "ACCELERATING"
        #if incoming ACK
        if inQRack is not None:
            if inQRack == "GREEN":
                return "LEAVING"
            else:#RED
                return "BRAKING"
