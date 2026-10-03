TARGETS: CheckGlobalThis.shouldReportThis(n,parent) classifies illegal top-level/unbound this; visit
emits diagnostics.
TARGETS: RuntimeTypeCheck.AddMarkers.visit/visitFunction and
AddChecks.visitReturn/createCheckTypeCallNode instrument type checks.
ORACLES: testIssue182a and testIssue182b assert exactly one JSError (expected 1, actual 0).
ORACLES: testValueWithInnerFn asserts RuntimeTypeCheck instrumentation of a value containing an
inner fn matches expected output.
CASES: top-level/unbound this inside anonymous fn, var-assigned fn, nested fn returning value -> one
error/check inserted.
CASES: this in object method/prototype fn/constructor/@this-annotated fn -> no error/skip.
CASES: IIFE returning this, inner fn as value, null/undefined/unknown JSType, empty body -> verify
skip/insert.
CASES: error/invalid/empty source or missing JSDoc type info -> should not throw or prune wrong
node.
RISKS: prompts omit exact test sources; infer JS snippets and expected generated code from
test/method names only.
RISKS: RuntimeTypeCheck insertion point after inner-fn declaration can shift order; do not validate
node count alone.