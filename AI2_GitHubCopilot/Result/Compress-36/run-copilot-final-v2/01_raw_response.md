TARGETS: SevenZFile.getNextEntry() entry tracking, read(), read(byte[],int,int), close().
ORACLES: After getNextEntry() for a zero-size entry, read()/read(byte[]) must return -1, not throw
IllegalStateException.
CASES: Normal entry with data; entry size exactly 0; multiple size-0 entries in sequence;
getNextEntry() returns null.
CASES: read or read(byte[]) without prior getNextEntry() → IllegalStateException; read after end of
entry.
RISKS: No visible SevenZOutputFile for programmatic creation of 7z archives with zero-size entries;
may need pre-existing test resources.