TARGETS: CSVFormat.print(Object, Appendable, boolean) escaping/quoting with configured escape and quote chars.
TARGETS: CSVFormat.format(Object...) output for null, backslash, and values requiring escape handling.
ORACLES: Trigger assertions: escaped null/backslash must not be surrounded by quote characters in cited cases.
ORACLES: Exact expected strings from failure report: \ and \\ versus erroneous "\"", "\\", '\', and '\\'.
CASES: Escape null with quote/escape configurations represented by trigger tests 1, 4, and 5.
CASES: Escape one and repeated backslashes with quote/escape configurations represented by trigger tests 1, 4, and 5.
CASES: Verify normal record formatting remains delimiter/record-separator correct when printing values.
RISKS: Available context truncates CSVFormat implementation and omits exact trigger setup/configuration.