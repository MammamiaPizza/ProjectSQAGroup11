TARGETS: ReachingUses constructors, equals/hashCode, ReachingUsesJoinOp.apply, local-use add/remove behavior.
ORACLES: Existing FlowSensitiveInlineVariablesTest.testIssue794b assertion and ReachingUses equality semantics.
CASES: Copy construction preserves reaching uses; equal/unequal states have consistent hash codes.
CASES: Join multiple predecessor states; add/remove local names; exception-handler CFG nodes.
RISKS: Private transfer helpers require exercising analysis indirectly through compiler/flow-sensitive inlining.
RISKS: No source-level expected output or CFG fixture details are provided beyond the failing test.