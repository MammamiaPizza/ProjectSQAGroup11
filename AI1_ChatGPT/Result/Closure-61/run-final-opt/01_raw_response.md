TARGETS: NodeUtil side-effect predicates used by PeepholeRemoveDeadCode call/useless-operation removal.  
ORACLES: Existing PeepholeRemoveDeadCodeTest::testCall1, testCall2, testRemoveUselessOps assertions.  
CASES: Calls whose results are unused; distinguish side-effecting versus removable call expressions.  
CASES: Useless expression/operator trees, including nested operands that may retain effects.  
RISKS: NodeUtil APIs are mostly package-private; tests likely require jscomp-package AST/compiler fixtures.  
RISKS: Context omits modified diff and exact AST inputs/expected transformed source.