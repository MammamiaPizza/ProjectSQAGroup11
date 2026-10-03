TARGETS: PrepareAst.process and traversal normalization/annotation paths.  
TARGETS: normalizeNodeTypes, normalizeBlocks, call, dispatcher, and object-literal annotation handling.  
ORACLES: Existing IntegrationTest.testIssue937 failure is the only stated expected-result source.  
CASES: Run process with externs/root shapes exercising blocks, typed nodes, calls, and object literals.  
CASES: Include null/empty or minimal AST roots only if supported by existing Node/Compiler conventions.  
RISKS: Private helpers require observing AST changes through process rather than direct invocation.  
RISKS: No failing input, asserted output, or Issue 937 semantics are provided in this context.