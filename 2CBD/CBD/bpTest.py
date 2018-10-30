from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw

# deriv = DerivatorBlock("deriv")
# draw(deriv, "deriv.dot")

# integr = IntegratorBlock("integrator")
# draw(integr, "integrator.dot")

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
    # draw(cbd, "delaytest.dot")

testDelayBlock()
