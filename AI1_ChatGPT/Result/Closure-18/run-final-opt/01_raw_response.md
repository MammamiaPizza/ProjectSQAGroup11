TARGETS: Compiler module initialization/compilation behavior exercised by IntegrationTest.testDependencySorting.  
ORACLES: Existing trigger assertion and Compiler results/output/module dependency ordering.  
CASES: Multiple JSModule inputs with dependencies requiring topological dependency sorting.  
CASES: Normal dependency chain and independent modules; verify emitted/processed order via public compilation output.  
CASES: Boundary ordering where a module has no dependencies or depends on an earlier/later listed module.  
RISKS: API excerpt omits compile/init signatures and trigger fixture details; derive setup only from available test sources.