
import charstream
import scanner

# Test Number FSA
# for inp in ["12", "12.3e+1", "12.3e1", "e3", "10ab", "10e3", "10E-3", ".10", ".10e3", "10.", "10.3e44"]:
# 	str=charstream.CharacterStream(inp)
# 	sc=scanner.NumberScanner(str)
# 	success=sc.scan()
#  	if success:
# 		print ">> recognized "+sc.__str__()
# 		print ">> committing"
# 		str.commit()
# 	else: 
# 		print ">> rejected"
# 		print ">> rolling back"
# 		str.rollback()
# 	print str
# 	print ""

# # Test CellRef FSA
# for inp in ["AAA", "A1", "b23", "ab$10", "$f$235", "ij$1"]:
	# str=charstream.CharacterStream(inp)
	# sc=scanner.CellRefScanner(str)
	# success=sc.scan()
 # 	if success:
	# 	print ">> recognized "+sc.__str__()
	# 	print ">> committing"
	# 	str.commit()
	# else: 
	#   print ">> rejected"
	#   print ">> rolling back"
	#   str.rollback()
	# print str
	# print ""

# Test UseCase3 FSA

# Test strings die moeten matchen: 
# 	"E 3R 1R 2X 3" = OK 
# 	"E 3##R 1##R 2##X 3" OK
#	"E 3#Een commentR 1#Nog een commentR 2#Nog een commentX 3" wordt gematched = OK
# 	"E 3\n#Een commentR 1\n#Nog een commentR 2\n#Nog een commentX 3" wordt gematched = OK
# 	"E 3\n#Een commentR 2\n#Nog een commentR 1\n#Nog een commentX 3" wordt gematched = OK

# Test strings die niet mogen matchen:
# 	"E 3\n#Een commentR 2\n#Nog een commentX 3" wordt NIET gematched = OK
# 	"E 3\n#Een commentR 1\n#Nog een commentX 3" wordt NIET gematched = OK
#	"E 3\n#Een commentX 3\n#Nog een commentR 1\n#Nog een commentX 3" wordt NIET gematched = OK
#	"E 3\n#Een commentX 3\n#Nog een commentR 2\n#Nog een commentX 3" wordt NIET gematched = OK
#	"E 3\n#Een commentX 3\n#Nog een commentR 2\n#Nog een commentR 1\n#Nog een commentX 3" wordt NIET gematched = OK

for inp in ["E 3\n#Een commentR 2\n#Nog een commentR 1\n#Nog een commentX 3"]:
	str=charstream.CharacterStream(inp)
	sc=scanner.UseCase3Scanner(str)
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