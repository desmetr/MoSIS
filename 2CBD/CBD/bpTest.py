from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw

# deriv = DerivatorBlock("deriv")
# draw(deriv, "deriv.dot")

# integr = IntegratorBlock("integrator")
# draw(integr, "integrator.dot")

def testNegatorBlockPos():
  cbd = CBD("CBD")
  cbd.addBlock(ConstantBlock(block_name="c1", value=6.0))
  cbd.addBlock(NegatorBlock(block_name="n"))
  cbd.addConnection("c1", "n")

  cbd.run(4)
  print _getSignal(cbd, "n")


def _getSignal(cbd, blockname, output_port = None):
  foundBlocks = [ block for block in cbd.getBlocks() if block.getBlockName() == blockname ]
  numFoundBlocks = len(foundBlocks)
  if numFoundBlocks == 1:
    signal =  foundBlocks[0].getSignal(name_output = output_port)
    return [x.value for x in signal]
  else:
    raise Exception(str(numFoundBlocks) + " blocks with name " + blockname + " found.\nExpected a single block.")


def testDelayBlock():
    cbd = CBD("CBD")
    cbd.addBlock(ConstantBlock(block_name="c1", value=5.0))
    cbd.addBlock(ConstantBlock(block_name="c2", value=3.0))
    cbd.addBlock(DelayBlock(block_name="d"))

    cbd.addConnection("c2", "d")
    cbd.addConnection("c1", "d", input_port_name="IC")

    cbd.run(4)
    print _getSignal(cbd, "d")
    print _getSignal(cbd, "d") == [5.0, 3.0, 3.0, 3.0]
    # draw(cbd, "delaytest.dot")

def initializeFuncDerBas():
    #f(t) = 5*t
    CBDFunc = CBD("function", output_ports = ["OUT1"])
    CBDFunc.addBlock(TimeBlock(block_name="t"))
    CBDFunc.addBlock(ProductBlock(block_name="p"))
    CBDFunc.addBlock(ConstantBlock(block_name="c", value=5.0))
    CBDFunc.addConnection("t", "p")
    CBDFunc.addConnection("c", "p")
    CBDFunc.addConnection("p", "OUT1")
    return CBDFunc

def testDerivatorBlock():
    cbd = CBD("CBD")
    cbd.addBlock(ConstantBlock(block_name="c3", value=1.0))
    cbd.addBlock(ConstantBlock(block_name="zero", value=0.0))
    CBDFunc = initializeFuncDerBas()
    cbd.addBlock(CBDFunc)
    der = DerivatorBlock(block_name="der")
    cbd.addBlock(der)

    cbd.addConnection("c3", "der", input_port_name="delta_t")
    cbd.addConnection("zero", "der", input_port_name="IC")
    cbd.addConnection("function", "der")

    # draw(der, "deriv.dot")
    # draw(CBDFunc, "deriv2.dot")
    # draw(cbd, "deriv3.dot")

    cbd.run(5)
    print _getSignal(cbd, "der"), [0.0]+[5.0]*4
testDerivatorBlock()
