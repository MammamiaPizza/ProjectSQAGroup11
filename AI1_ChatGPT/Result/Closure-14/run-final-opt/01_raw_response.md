TARGETS: ControlFlowAnalysis.process/visit CFG construction for try/finally, nested finally, break, and return paths.  
TARGETS: handleTry, handleBreak, handleReturn, computeFollowNode, and exception-handler/finally routing.  
ORACLES: Existing triggers: no JSC_MISSING_RETURN_STATEMENT for Issue779 function expected to return number.  
ORACLES: Existing CFG tests require cross edges for deeply nested finally and break-with-finally control flow.  
CASES: Function return inside/through nested try/finally; ensure all reachable paths preserve return completion.  
CASES: Deep nesting of finally blocks with break crossing scopes; assert CFG cross edges are present.  
CASES: Nested finally without break; verify edges route through each finally before its follow node.  
RISKS: ControlFlowAnalysis is package-private; tests likely need same package or existing compiler test harness.  
RISKS: Context lacks exact AST/CFG assertion APIs and source fixtures; derive expectations only from stated triggers.