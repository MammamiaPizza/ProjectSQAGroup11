TARGETS: FlowSensitiveInlineVariables.visit, canInline, inlineVariable; GatherCandiates.apply;
Candidate.getDefinition
ORACLES: Compare optimized JS code with expected output; assertion on Node tree after inlining
CASES: Simple for-in with candidate that can/should inline; for-in where var is reassigned (should
not inline)
CASES: For-in with break/continue; nested for-in; variable declared inside loop; empty loop body
RISKS: Exact bug semantics unknown without source; test on for-in var reassign patterns; may need
additional deps