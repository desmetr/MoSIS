import charstream
import scanner

with open('traceNumber.txt') as f:
    inputLines = f.readlines()

# Test Number FSA
# for inp in ["12", "12.3e+1", "12.3e1", "e3", "10ab", "10e3", "10E-3", ".10", ".10e3", "10.", "10.3e44"]:
for inp in inputLines:
	str=charstream.CharacterStream(inp)
	sc=scanner.NumberScanner(str)
	success=sc.scan()
 	if success:
		print ">> recognized "+sc.__str__()
		print ">> committing"
		str.commit()
	else: 
		print ">> rejected"
		print ">> rolling back"
		str.rollback()
	print str
	print ""

# Test CellRef FSA
# for inp in ["AAA", "A1", "b23", "ab$10", "$f$235", "ij$1"]:
# 	str=charstream.CharacterStream(inp)
# 	sc=scanner.CellRefScanner(str)
# 	success=sc.scan()
#  	if success:
# 		print ">> recognized "+sc.__str__()
# 		print ">> committing"
# 		str.commit()
# 	else: 
# 	  print ">> rejected"
# 	  print ">> rolling back"
# 	  str.rollback()
# 	print str
# 	print ""