TARGETS: GatherSignatures.visit() handling of GETPROP for method calls vs property accesses
TARGETS: addPossibleSignature() decision to skip non-method property assignments
TARGETS: RenameVars.okToRenameVar check for externNames like $export$ and $super$
ORACLES: testIssue2508576_1 expects getter inline result; testSeparateMethods expects 0 errors
ORACLES: testDollarSignSuperExport2 expects $export$ prefix unchanged after renaming
CASES: getter property used with no arguments → not a signature, inline succeeds
CASES: function called with 3 args when defined with 1–2 args → JSC_WRONG_ARGUMENT_COUNT
CASES: extern method accessed as property (not a call) → no signature added
CASES: $export$ and $super$ names in externNames not renamed
RISKS: Only buggy context available; oracle inferred from test failure descriptions; ambiguous
corner cases