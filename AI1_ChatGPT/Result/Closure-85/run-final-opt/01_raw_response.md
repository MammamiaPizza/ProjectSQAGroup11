TARGETS: process/visit elimination and tryRemoveUnconditionalBranching cascaded branch removal.
TARGETS: computeFollowing control-flow successor handling; safe dead expression-statement removal.
ORACLES: Existing trigger tests testCascadedRemovalOfUnlessUnconditonalJumps and testIssue311.
CASES: Cascaded unreachable code after unconditional jumps, including nested/control-flow successors.
CASES: Issue311 input path must complete without INTERNAL COMPILER ERROR.
RISKS: Private helpers require behavior observation via process/compiler output, not direct invocation.
RISKS: Context omits source fixtures and exact transformed JavaScript expectations.