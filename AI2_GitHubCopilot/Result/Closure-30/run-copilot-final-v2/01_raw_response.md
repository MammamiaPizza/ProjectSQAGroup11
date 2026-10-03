TARGETS: FlowSensitiveInlineVariables.Candidate.canInline(),
MustBeReachingVariableDef.computeDependence(), MustDef.join()
ORACLES: Expected outputs from failing tests: testInlineAcrossSideEffect1,
testCanInlineAcrossNoSideEffect, testIssue698
CASES: Inline across side-effectful call (should NOT), across no-side-effect call (should), across
property assignment, after conditional, in loop
CASES: Variable defined in if/else and used after; multiple uses across blocks; redefinition after
use
RISKS: Must deduce expected inlining from test source only; non-trivial data-flow analysis may hide
edge-case dependencies