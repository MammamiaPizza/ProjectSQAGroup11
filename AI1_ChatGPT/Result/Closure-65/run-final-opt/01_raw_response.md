TARGETS: CodeGenerator string escaping, especially NUL handling in generated double-quoted JS strings.
ORACLES: CodePrinterTest::testZero expects `var x="\000"`; current output is `var x="\0"`.
CASES: Generate code for a string containing NUL and assert the three-digit octal escape is emitted.
CASES: Cover NUL adjacent to digits to ensure escaping remains unambiguous.
CASES: Cover ordinary printable strings and existing quote/backslash escaping for regressions.
RISKS: Escaping helpers are private; test through available code-printing behavior rather than direct calls.
RISKS: No other version or broader escaping specification is provided beyond the failing expected output.