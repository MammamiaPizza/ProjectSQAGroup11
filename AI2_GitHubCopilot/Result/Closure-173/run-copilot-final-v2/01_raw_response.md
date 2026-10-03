TARGETS: CodeGenerator.addExpr (precedence-based paren insertion),
PeepholeSubstituteAlternateSyntax.optimizeSubtree (paren-safety during folds)
ORACLES: expected strings from testPrint, testIssue1062, testAssocitivity failures
CASES: binary ops (||, , |, %) inside array literal (e.g., 3[4%3*5], a||[b||c]), nested
same-precedence ops, non-assoc edge
RISKS: limited insight into internal precedence/context mapping; only visible failure examples;
other operator combos (&&, /) may be untested