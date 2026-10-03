TARGETS: RemoveUnusedVars.process and unused-variable/reference traversal behavior  
TARGETS: removeUnreferencedFunctionArgs and CallSiteOptimizer.optimize/applyChanges  
ORACLES: RemoveUnusedVarsTest::testIssue618_1 assertion outcome  
CASES: Unreferenced function arguments with call sites eligible for signature updates  
CASES: Referenced arguments must remain; nonmodifiable call sites must prevent unsafe removal  
CASES: Assignments and function scopes affecting whether a Var is referenced/removable  
RISKS: Source and failing test body are unavailable; exact Issue 618 expected output is unknown