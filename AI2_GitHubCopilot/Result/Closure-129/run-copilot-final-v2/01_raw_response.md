TARGETS process() triggers normalizeNodeTypes & normalizeBlocks; check after-process AST shape.
TARGETS PrepareAnnotations.visit: normalizeObjectLiteralAnnotations, annotateCalls,
annotateDispatchers.
ORACLES Compare post-process AST with expected normalized/annotated nodes from
IntegrationTest.testIssue937.
ORACLES After process, OBJECTLIT nodes gain annotations; call sites reflect dispatcher annotations.
CASES Normal: object literals, call expressions, dispatcher patterns; verify annotation presence and
accuracy.
CASES Boundaries: empty script, single-node tree, nested blocks, mixing annotated/non-annotated
nodes.
CASES Error: null/empty externs or root; verify graceful handling (no crash, no change).
RISKS Private helpers (reportChange, normalizeNodeTypes) unreachable directly; test via public
process().
RISKS Without exact bug description, tests may miss regression trigger; need reconstruction from
testIssue937.
RISKS Expected annotation behavior not fully documented; oracle must be derived from passing
IntegrationTest snapshot.