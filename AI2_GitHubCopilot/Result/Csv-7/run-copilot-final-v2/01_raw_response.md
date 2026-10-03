TARGETS: CSVParser.getHeaderMap() -> initializeHeader() must detect duplicate header column names
TARGETS: parse(String/File, CSVFormat) when format header (or first record) repeats a column name
ORACLES: testDuplicateHeaderEntries expects java.lang.IllegalStateException (CSV-112)
ORACLES: unique header names yield 1:1 name -> column-index mapping in header map
CASES: repeated identical names (e.g. "a","a") -> IllegalStateException
CASES: duplicates in format-supplied header vs duplicates taken from first record
CASES: single/zero-column header (no duplicates) -> no exception, correct map
CASES: case-only differences if comparison uses equalsIgnoreCase
RISKS: excerpt shows only addRecordValue/initializeHeader fragments; duplicate-comparison logic and
throw trigger point unverified
RISKS: assert exception type only; message text is unspecified