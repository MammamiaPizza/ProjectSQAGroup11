TARGETS: CSVParser.iterator(), Iterator.hasNext(), Iterator.next(), and parser record-number progression  
ORACLES: Trigger test expects record number 3, not 4, after iterator sequence interruption  
CASES: Call hasNext() repeatedly before next(); verify no extra record is consumed  
CASES: Interleave next() and hasNext(); verify returned CSVRecord sequence and getRecordNumber()  
CASES: End-of-input: hasNext() false and next() behavior per existing iterator contract  
RISKS: Iterator buffering may advance parser state twice or skip records  
RISKS: Context supplies only one failing expectation; exact input/format and next() EOF oracle are unavailable