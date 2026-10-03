TARGETS: CheckGlobalThis.shouldTraverse, visit, shouldReportThis, and getFunctionJsDocInfo.
ORACLES: Trigger tests define expected JSC_USED_GLOBAL_THIS diagnostic counts (zero or one).
CASES: Static-function contexts 6/7 expect no diagnostic; static-function context 8 expects one.
CASES: Global-this context 7 and static-method contexts 2/3 expect one diagnostic.
CASES: Inner-function contexts 1/2/3 expect one diagnostic.
RISKS: Failure details omit source snippets and full bug summary; exact AST/JSDoc conditions are unavailable.