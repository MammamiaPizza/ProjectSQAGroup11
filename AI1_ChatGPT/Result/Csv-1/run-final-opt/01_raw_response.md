TARGETS: ExtendedBufferedReader.read(), read(char[],int,int), readLine() line-number handling for CR.  
ORACLES: Trigger expects line number 1 after CR; reader return values and BufferedReader/Reader semantics.  
CASES: read single '\r', then verify line number; CRLF and LF normal line-ending boundaries.  
CASES: bulk read buffers containing CR at offset/length boundaries; verify returned count and line tracking.  
CASES: readLine() with CR, CRLF, LF, empty line, and EOF after terminator.  
RISKS: Package-private class/line-number access may constrain direct tests; only listed methods are available.