TARGETS: BuildGlobalNamespace.visit() for CATCH nodes; shouldTraverse() to push catch scope.
ORACLES: CheckGlobalNamesTest.testGlobalCatch expects no JSC_UNDEFINED_NAME for catch param e.
CASES: Catch param referenced inside catch block; unreferenced; nested try-catch; catch in global,
function, and module scope.
CASES: Multiple catch clauses; catch param shadowing a global variable; catch without body.
RISKS: Must not break handling of function/block scopes; ensure catch param is not treated as global
reference.