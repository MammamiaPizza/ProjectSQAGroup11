TARGETS: IR.function(name,params,body) with paramList variants; IR.tryCatch(tryBody,catchNode);
IR.block; IR.empty
TARGETS: Private validation checks mayBeStatement, mayBeExpression (trigger IllegalStateException)
ORACLES: Factory methods must not throw IllegalStateException for valid AST nodes (correct tokens,
non-null children)
ORACLES: For illegal child types, factory may throw but should not on correct nodes (e.g., body
block)
CASES: function("f",paramList(),block()) should return FUNCTION node with paramlist and block
children
CASES: function("f",paramList(),empty()) – body type mismatch if empty() not a statement; this may
cause bug
CASES: tryCatch(block(),catchNode(name("e"),block())); tryCatch with empty blocks; tryFinally with
empty blocks
CASES: paramList() empty params; paramList(singleNode); block() empty; block(singleStatement);
multi-statement block
RISKS: Exact trigger not visible; testIssue727 might involve constructing function with empty body
via empty()
RISKS: IR's validation may incorrectly reject empty() as body, causing IllegalStateException in
valid case