TARGETS: IRFactory transformation of incomplete function syntax; NodeTraversal.traverseFunction child handling  
ORACLES: IntegrationTest.testIncompleteFunction must not throw INTERNAL COMPILER ERROR  
CASES: Parse/traverse a complete function as normal control  
CASES: Parse/traverse incomplete function declarations/expressions with missing components  
CASES: Verify traversal tolerates absent function name, args, or body if IR can contain them  
RISKS: IRFactory and NodeTraversal internals are mostly private; test via compiler/integration entry points  
RISKS: Exact parser recovery AST shape is not provided; do not assert invented node structure