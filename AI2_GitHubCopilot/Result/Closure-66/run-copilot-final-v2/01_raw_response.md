TARGETS: doPercentTypedAccounting, ensureTyped, visitAssign, visitCall, visitGetProp, visitFunction
ORACLES: testGetTypedPercent5 and testGetTypedPercent6 expect 100.0% typed percent
CASES: fully typed script; script with missing JSType on some property reads/assigns
CASES: edge: no nodes; all nodes null-typed; mixed typed/untyped in same compilation
CASES: property access after an assignment, function calls with typed arguments, var decls
RISKS: Cannot see getTypedPercent() implementation or full test harness; need Closure test infra