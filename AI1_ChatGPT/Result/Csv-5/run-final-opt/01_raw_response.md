TARGETS: CSVPrinter.println() with CSVFormat whose record separator is null.
TARGETS: printRecord(Iterable/Object...) record termination behavior via println().
ORACLES: Trigger test CSVFormatTest::testNullRecordSeparatorCsv106 is the expected-result source.
CASES: Print values then println with null separator; verify no unintended separator output/failure.
CASES: printRecord with null separator; verify fields are printed and termination follows println behavior.
CASES: Non-null separator control case to distinguish normal record-ending behavior.
RISKS: Exact expected output for null separator is not provided beyond the named trigger failure.
RISKS: CSVFormat construction/configuration APIs are not included in the available context.