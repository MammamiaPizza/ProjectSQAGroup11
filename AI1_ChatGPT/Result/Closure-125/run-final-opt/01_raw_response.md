TARGETS: TypeCheck.process/processForTesting/check traversal, especially testIssue1002 path.  
ORACLES: Existing TypeCheckTest::testIssue1002 expects no IllegalStateException.  
CASES: Reproduce Issue 1002 input through the existing compiler/test harness.  
CASES: Verify traversal completes and reports only harness-defined diagnostics/results.  
RISKS: Private visit* helpers are not directly testable via listed public API.  
RISKS: No source/input fixture or expected diagnostics are provided beyond exception absence.