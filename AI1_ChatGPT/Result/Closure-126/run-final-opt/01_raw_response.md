TARGETS: MinimizeExitPoints.process(Node,Node) and visit(NodeTraversal,Node,Node) AST exit minimization  
TARGETS: matchingExitNode(Node,int,String) matching of exit type and optional label  
ORACLES: Existing trigger-test assertions define expected transformed output/exit preservation  
CASES: Function return optimization covered by testFunctionReturnOptimization  
CASES: break inside try/finally must remain covered by testDontRemoveBreakInTryFinally  
RISKS: Moving/removing exits can alter finally execution or labeled-break control flow  
RISKS: No source body or test expected-output snippets are provided; derive cases from existing tests only