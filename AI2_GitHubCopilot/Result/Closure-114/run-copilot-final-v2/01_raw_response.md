TARGETS: test removal of assignment-with-call patterns in NameAnalyzer.process
TARGETS: verify recordSet/recordAlias and reference counting on call RHS
ORACLES: compare expected output JS after DeadCodeElimination (original vs. processed AST)
ORACLES: test suite NameAnalyzerTest expected results define correct retention
CASES: normal: x=foo(); x used later → retain both
CASES: boundary: x=foo(); x unused → may be removable if RHS has no side effects
CASES: boundary: a.b=func() where b is prototype property → depends on class-defining status
CASES: error: nested assignments (x=y=func()) – verify only necessary parts kept
CASES: assign-and-call chain: x=a(), y=b(x) → ensure no premature removal
RISKS: incomplete knowledge of internal alias/prototype handling may miss error paths