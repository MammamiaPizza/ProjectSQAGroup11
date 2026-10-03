TARGETS: CodeGenerator expression printing/parenthesization for nested ||, *, |, and % under multiplication.
TARGETS: PeepholeSubstituteAlternateSyntax.optimizeSubtree associativity-sensitive rewrites.
ORACLES: Existing CodePrinterTest expected strings for testPrint and testIssue1062.
ORACLES: Existing PeepholeSubstituteAlternateSyntaxTest::testAssocitivity assertions.
CASES: Print a||(b||c), a*(b*c), a|(b|c): retain required nested parentheses.
CASES: Print 3*(4%3*5): retain parentheses around the nested mixed-precedence expression.
CASES: Exercise optimizer associativity behavior covered by testAssocitivity.
RISKS: APIs are mostly private; tests likely need existing compiler/parser test harnesses.
RISKS: No source-level diff or full test bodies provided; avoid inferring additional rewrite semantics.