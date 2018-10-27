from bokeh.plotting import figure, output_file, show
from CBDMultipleOutput.Source.CBD import *
from CBDMultipleOutput.Source.CBDDraw import draw

deriv = DerivatorBlock("deriv")
draw(deriv, "deriv.dot")

integr = IntegratorBlock("integrator")
draw(integr, "integrator.dot")

def testDelayBlock():
    cbd = CBD("CBD")
    cbd.addBlock(ConstantBlock(block_name="c1", value=5.0))
    cbd.addBlock(ConstantBlock(block_name="c2", value=3.0))
    cbd.addBlock(DelayBlock(block_name="d"))

    cbd.addConnection("c2", "d")
    cbd.addConnection("c1", "d", input_port_name="IC")

    print(cbd.run(4))

testDelayBlock()
