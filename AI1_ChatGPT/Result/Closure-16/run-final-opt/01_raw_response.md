TARGETS: ScopedAliases.process and hotSwapScript; traversal alias discovery, namespace-shadow renaming, type-node fixing  
ORACLES: Existing testIssue772 expects no compiler warnings/errors and an expected transformed AST  
CASES: Reproduce Issue 772 input through ScopedAliasesTest and IntegrationTest compilation paths  
CASES: Verify alias usages are rewritten after scope traversal and scope-call validation remains error-free  
CASES: Exercise namespace-shadow detection/renaming where aliases and local names collide  
CASES: Include type nodes referencing aliases, since fixTypeNode participates in traversal  
RISKS: Exact Issue 772 source and expected AST are not provided; derive assertions only from existing test fixtures  
RISKS: Avoid assuming behavior for malformed scope calls beyond existing validation/error expectations