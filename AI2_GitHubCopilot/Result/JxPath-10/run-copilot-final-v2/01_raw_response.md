TARGETS: CoreOperationRelationalExpression.compute(Object left, Object right) – handling empty
Iterator/NodeSet
ORACLES: Per XPath 1.0 existential semantics, empty node-set vs any value must return false for all
comparisons
CASES: left empty >0, >=0, <0, <=0, =0, !=0; left empty vs string; right empty; both empty;
single-node (true/false); multi-node (existential true)
RISKS: cannot inspect buggy code; bug might reside in reduce() or containsMatch() only triggered by
relational ops; trust trigger test only