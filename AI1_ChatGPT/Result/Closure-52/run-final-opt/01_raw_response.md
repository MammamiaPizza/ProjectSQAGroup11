TARGETS: CodeGenerator property-key emission, especially numeric-looking string keys in object literals.
ORACLES: Trigger expects `var x={["010"]:1}`; buggy output is `var x={[10]:1}`.
CASES: String key `"010"` must remain quoted/bracketed rather than converted to numeric key `10`.
CASES: Cover numeric-looking keys through existing CodePrinterTest numeric-key printing path.
RISKS: Leading-zero text is semantically distinct from normalized numeric text in generated source.
RISKS: Context provides only one failing key; broader numeric-key expectations are unspecified.