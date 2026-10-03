TARGETS: CSVFormat.withHeader(String...), CSVFormat.validate(), CSVFormat.parse(Reader)
TARGETS: Interaction of allowMissingColumnNames with duplicate header detection
ORACLES: testExcelHeaderCountLessThanData expects no exception when header count < data
ORACLES: Empty-string headers allowed when allowMissingColumnNames=true (not duplicates)
CASES: header ["A","B","C","",""] with extra data column -> parse without exception
CASES: header ["X","Y","X"] duplicate non-empty -> must throw IllegalArgumentException
CASES: header [] parsing; all-empty header ["",""] (allowMissing=true); boundary large header
RISKS: Buggy validate treats empty strings as duplicates; fix logic not visible; risk regression on
non-empty dup
RISKS: Excel default allowMissingColumnNames=true; need to confirm; RFC4180 may differ
RISKS: Private validate() only indirectly testable; test must use parse() or public construction