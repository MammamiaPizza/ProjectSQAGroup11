TARGETS: CodeGenerator object-literal property key emission; addExpr/addJsString and isSimpleNumber paths.
ORACLES: Existing trigger CodePrinterTest::testIssue942 expects `var x={0:1}` for source key `"0"`.
CASES: Object literal with string key `"0"` and numeric value `1`; verify generated JavaScript omits key quotes.
CASES: Boundary numeric-like keys should follow the generator's simple-number classification, where observable.
RISKS: Context lacks patch details and public construction/printing APIs; derive assertions from the existing test infrastructure.