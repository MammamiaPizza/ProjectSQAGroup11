TARGETS: ScopedAliases.Traversal.visit,fixTypeNode; Scope.getVar,Scope.Var.toString
ORACLES: ScopedAliasesTest.testForwardJsDoc expected string; Var has correct name & source file
after alias resolution
CASES: alias used before define (forward) in same scope, after define, nested alias chains, alias in
global vs local scope, missing alias (error)
RISKS: assertion tied to Var.toString format; may miss other forward JSDoc usages; fix could regress
non‑JSDoc alias handling