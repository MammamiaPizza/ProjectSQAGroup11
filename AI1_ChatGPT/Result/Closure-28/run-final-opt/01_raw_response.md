TARGETS: InlineCostEstimator.getCost(Node) and getCost(Node,int) AST size estimation.
ORACLES: Existing trigger testCost expects cost 1 where buggy result is 4.
ORACLES: Existing InlineFunctionsTest.testIssue728 is integration-level expected behavior.
CASES: Identifier/name nodes whose emitted representation should have minimal estimated cost.
CASES: Normal ASTs and threshold-boundary inputs for overloaded getCost.
CASES: Costs at, below, and above costThreshhold; verify threshold handling.
RISKS: Estimation is implemented through private CompiledSizeEstimator/CodeConsumer output behavior.
RISKS: No source-level expected costs or AST-construction API details are provided.