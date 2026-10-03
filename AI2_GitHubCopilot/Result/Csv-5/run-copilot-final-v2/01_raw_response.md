TARGETS: println() when format.getRecordSeparator()==null; printRecord() calling println;
printComment() may also call println.
ORACLES: Assert no NPE; null separator ideally appends nothing; check via getOut() content.
CASES: Normal: separator="\n"; Boundary: null separator, empty separator; multiple records; with
comments; flush/close after null separator.
RISKS: Undocumented null handling; may throw exception; may append literal "null"; rely on test
expectations.