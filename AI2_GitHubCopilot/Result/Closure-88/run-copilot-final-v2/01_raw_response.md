TARGETS: DeadAssignmentsElimination.apply, visit; decisions on local var assignments inside for-in
loops.
ORACLES: trigger tests issue297a-f expect assignments in for-in loops to survive elimination;
CommandLineRunnerTest same.
CASES: var declared before for-in, assigned inside loop, used after — must keep.
CASES: var assigned inside for-in but never read — can eliminate.
CASES: for-in with continue/break containing assignment; nested for-in; var initialized in loop body
but only used before reassignment.
RISKS: false positive removal of live assignments when variable is referenced in later statements
outside loop.
RISKS: insufficient test for complex control flow (returns, throws) within for-in affecting liveness
analysis.