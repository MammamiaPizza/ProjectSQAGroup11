TARGETS: SevenZFile.getNextEntry(), read(), read(byte[]), read(byte[],int,int), getCurrentStream()
ORACLES: Existing trigger SevenZFileTest::readEntriesOfSize0 and COMPRESS-348 failure report
CASES: Iterate to a zero-size 7z entry, then read it without IllegalStateException
CASES: Verify zero-size entry read behavior for single-byte and byte-array read overloads
RISKS: Context omits archive fixtures and exact expected EOF values/assertions
