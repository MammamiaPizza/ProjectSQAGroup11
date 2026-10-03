TARGETS: LiveVariablesAnalysis handling of expressions in for-in constructs.  
TARGETS: addToSetIfLocal(Node, BitSet) and local-variable indexing/liveness updates.  
ORACLES: Trigger test must complete without IllegalStateException.  
ORACLES: Existing LiveVariableAnalysisTest expectations are the result source.  
CASES: for-in with an expression requiring local-variable liveness processing.  
CASES: Local versus non-local names encountered in the for-in expression.  
RISKS: Available context omits AST construction and exact expected live-variable sets.