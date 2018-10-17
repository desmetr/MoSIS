
import charstream
import scannerUseCase3 as scanner

# Test UseCase3 FSA

# Test strings die moeten matchen:
# 	"E 3R 1R 2X 3" wordt gematched = OK
#	"E 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nX 3\n#Nog een comment\n" wordt gematched = OK
#	"E 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nE 1\n#Nog een comment\nX 3\n#Nog een comment\n" wordt gematched = OK -> extra trein op 1 tijdens deze use case
#	"E 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nE 2\n#Nog een comment\nX 3\n#Nog een comment\n" wordt gematched = OK -> extra trein op 2 tijdens deze use case
#	"E 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nE 2\n#Nog een comment\nE 1\n#Nog een comment\nX 3\n#Nog een comment\n" wordt gematched = OK
#	"E 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nE 1\n#Nog een comment\nE 2\n#Nog een comment\nX 3\n#Nog een comment\n" wordt gematched = OK
#	"E 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nX 3\n#Nog een comment\nE 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nX 3\n#Nog een comment\n"
# 			wordt gematched = OK

# Test strings die niet mogen matchen:
# 	"E 3##R 1##R 2##X 3" wordt NIET gematched = OK (geen newlines)
#	"E 3#Nog een commentR 1#Nog een commentR 2#Nog een commentX 3" wordt NIET gematched = OK (geen newlines)
# 	"E 3\n#Nog een commentR 1\n#Nog een commentR 2\n#Nog een commentX 3" wordt NIET gematched = OK (geen newlines)
# 	"E 3\n#Nog een commentR 2\n#Nog een commentR 1\n#Nog een commentX 3" wordt NIET gematched = OK (geen newlines)
#	"E 3\n#Nog een commentR 2\n#Nog een commentR 1\n#Nog een commentX 3\n#Nog een comment" wordt NIET gematched = OK (niet overal newlines)
# 	"E 3\n#Nog een commentR 2\n#Nog een commentX 3" wordt NIET gematched = OK
# 	"E 3\n#Nog een commentR 1\n#Nog een commentX 3" wordt NIET gematched = OK
#	"E 3\n#Nog een commentX 3\n#Nog een commentR 1\n#Nog een commentX 3" wordt NIET gematched = OK
#	"E 3\n#Nog een comment\nX 3\n#Nog een comment\nR 2\n#Nog een comment\nX 3\n" wordt NIET gematched = OK
#	"E 3\n#Nog een comment\nX 3\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nX 3\n" wordt NIET gematched = OK

# with open('../trace.txt') as f:
with open('../testtrace.txt') as f:
    traceInput = f.read()

# for inp in ["E 3\n#Nog een comment\nR 1\n#Nog een comment\nR 2\n#Nog een comment\nR 1\n#Nog een comment\nR 2\n#Nog een comment\nX 3\n#Nog een comment\n"]:
for inp in [traceInput]:
	str=charstream.CharacterStream(inp)
	sc=scanner.UseCase3Scanner(str)
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
