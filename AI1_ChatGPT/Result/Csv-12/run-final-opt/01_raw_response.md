TARGETS: CSVFormat.withHeader(String...) validation of duplicate and missing/empty header names.
ORACLES: CSV-128 trigger expects EXCEL header [A,B,C,"",""] to be accepted for parsing data.
CASES: EXCEL.withHeader("A","B","C","","") should not throw IllegalArgumentException.
CASES: Duplicate non-empty headers should retain duplicate-name rejection unless missing names are allowed.
CASES: Compare repeated empty names with allowMissingColumnNames false versus true.
RISKS: Context lacks CSVFormat implementation and parser/header mapping assertions beyond the trigger exception.