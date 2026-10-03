TARGETS: CodeGenerator.jsString and string-literal emission used by CodePrinterTest::testUnicode.
ORACLES: Trigger expects U+007F encoded as "\\u007f" in output: var x="\\u007f".
CASES: Emit strings containing U+007F; verify surrounding printable ASCII remains unchanged.
CASES: Boundary-check nearby control/ASCII characters only where existing tests/specification provide expectations.
RISKS: CharsetEncoder-dependent escaping may affect Unicode output; no alternate version/spec details are available.