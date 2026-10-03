TARGETS: computeValue(EvalContext); relational comparison path for variable-derived arithmetic operands.  
TARGETS: compute(Object,Object), reduce(Object), containsMatch(Iterator,Object), findMatch(Iterator,Iterator).  
ORACLES: JXPath149Test expects <$a + $b <= $c> to evaluate true.  
CASES: Variables with numeric values where sum equals right operand; verify <= equality boundary.  
CASES: Sum below and above right operand to distinguish relational outcomes.  
CASES: Scalar versus iterator/collection operands if reachable through expression evaluation.  
RISKS: Abstract evaluateCompare(int) determines operator-specific assertions; concrete operator context is not provided.  
RISKS: No non-buggy version or further expected semantics available; derive expectations only from trigger/spec.