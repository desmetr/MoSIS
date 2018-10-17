
import charstream
import scannerUseCase4 as scanner

with open('../trace.txt') as f:
# with open('../testtrace.txt') as f:
    traceInput = f.read()

# traceInput1 = "E 1\n"
# traceInput2 = "E 1\nE 2\n"
# traceInput3 = "E 1\nE 2\nG 1\n"
# traceInput4 = "E 1\nE 2\nG 1\nG 2\n"
# traceInput5 = "E 1\nG 1\n"
# traceInput6 = "E 2\nG 2\n"
# traceInput7 = "E 1\nE 2\nG 2\nG 1\n"
# traceInput8 = "E 2\nE 1\nG 1\nG 2\n"

for inp in [traceInput]:
	str=charstream.CharacterStream(inp)
	sc=scanner.ScannerUseCase4(str)
	success=sc.scan()
 	if success:
		print ">> recognized "+sc.__str__()
		print ">> committing"
		str.commit()
	else:
	  print ">> rejected in state " + sc.current_state
	  print ">> rolling back"
	  str.rollback()
	# print str
	print ""
