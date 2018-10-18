
import charstream
import scannerUseCase3 as scanner

# Run UseCase3 FSA on the trace file.

with open('../trace.txt') as f:
    traceInput = f.read()

for inp in [traceInput]:
	stream=charstream.CharacterStream(inp)
	sc=scanner.UseCase3Scanner(stream)
	success=sc.scan()
 	if success:
		print ">> recognized "+sc.__str__()
		print ">> committing"
		stream.commit()
	else:
	  print ">> rejected in state " + sc.current_state
	  print ">> rolling back"
	  print ">> found " + str(sc.counter) + " occurences"
	  stream.rollback()
	# print str
	print ""