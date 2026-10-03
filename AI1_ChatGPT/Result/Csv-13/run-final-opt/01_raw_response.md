TARGETS: CSVFormat.MYSQL null-string configuration and CSVFormat.withNullString(String)
TARGETS: CSVPrinter.print(Object) null handling; printRecord(Object...) field output
ORACLES: Trigger testMySqlNullStringDefault expects MYSQL null output as \N, not "null"
ORACLES: Trigger testMySqlNullOutput supplies expected output for configured null-string behavior
CASES: Print a null value with CSVFormat.MYSQL default null string
CASES: Print null beside non-null fields to verify delimiter/record formatting is preserved
CASES: Print null after MYSQL.withNullString(...) and verify configured representation
RISKS: Null output may be converted through String.valueOf, producing literal "null"
RISKS: Context omits full source and exact custom-null expected string from the trigger assertion