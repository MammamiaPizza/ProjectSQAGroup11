TARGETS: RemoveUnusedVars.process() and removal of unreferenced variables, including function args.
TARGETS: CallSiteOptimizer.optimize() and canRemoveArgFromCallSites() for issue-618 scenario.
ORACLES: Expected output string in test (using CompilerTestCase.test(src, expected)).
CASES: Normal removal of unused local vars; removal of unused function params after call site
optimization.
CASES: Boundary: var referenced only in property access (x.prop); not used as direct reference.
CASES: Boundary: var used in typeof check; should not be removed.
CASES: Error: var used in eval() string; hard to determine reference, might be incorrectly removed.
RISKS: Unknown exact test input for testIssue618_1; must infer required guarding behavior.
RISKS: Interaction with other passes (e.g., inlining) might mask or alter the bug condition.