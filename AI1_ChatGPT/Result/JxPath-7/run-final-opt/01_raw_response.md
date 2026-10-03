TARGETS: computeValue(EvalContext) for >, >=, <, <= and relational-expression shared behavior.
ORACLES: Existing CoreOperationTest.testNodeSetOperations; JXPATH-93 expects <$array > 0> true.
CASES: Node-set/array numeric comparison where at least one value is greater than scalar 0.
CASES: Cover all four operators with matching, nonmatching, equality, and empty node-set/array values.
RISKS: Relational semantics may require existential node-set comparison rather than scalar coercion.
RISKS: Context construction and Expression operand APIs are not provided; avoid assuming them.