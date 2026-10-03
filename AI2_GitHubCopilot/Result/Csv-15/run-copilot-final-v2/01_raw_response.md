TARGETS: CSVFormat.printAndEscape quoting logic; CSVPrinter.printRecord for per-value quoting
decisions.
ORACLES: Expected output from CSVPrinterTest::testDontQuoteEuroFirstChar; spec: no quoting when
first char is a non-special printable (e.g., €).
CASES: Value starting with €; values starting with delimiter, quote, escape char; empty value;
single-char; mixed first-char special/non-special.
RISKS: Partial context (only one trigger); need to cover multiple CSVFormat configs and QuoteMode to
avoid regressions.