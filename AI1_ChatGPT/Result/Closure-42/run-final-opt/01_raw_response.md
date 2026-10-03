TARGETS: IRFactory transformation for ParserTest::testForEach, especially for-each AST/IR construction.  
ORACLES: Existing ParserTest::testForEach assertions are the only stated expected-result source.  
CASES: Normal for-each forms accepted by the parser and transformed into expected IR.  
CASES: Boundary variants exercised by testForEach, including declaration versus assignment iteration targets.  
RISKS: IRFactory methods are private; testing likely requires parser-level integration rather than direct calls.  
RISKS: Available context omits exact test inputs/assertions and transformed token structure.