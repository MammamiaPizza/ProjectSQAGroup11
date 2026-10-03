TARGETS: tryFoldGetElem and tryFoldArrayAccess for constant array element access.
ORACLES: Existing testFoldGetElem expects zero compiler errors for the triggering source.
CASES: Array get-element with numeric index 0, the reported failing index.
CASES: Boundary array indices only where existing tests/specification define expected output.
RISKS: Folding must not emit JSC_INDEX_OUT_OF_BOUNDS_ERROR for the trigger.
RISKS: Methods are private; test through the existing compiler/test harness.
RISKS: No source snippet or intended folded AST/result is provided beyond the zero-error oracle.