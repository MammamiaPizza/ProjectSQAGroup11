TARGETS: CoreOperationRelationalExpression.computeValue and relational evaluation with empty node sets.  
ORACLES: Existing CoreOperationTest.testEmptyNodeSetOperations assertion for </idonotexist >= 0> == false.  
CASES: Empty-node-set versus numeric literal for >=; verify false rather than true.  
CASES: Empty-node-set with other relational operators if covered by existing expression evaluation APIs.  
CASES: Normal nonempty node-set comparisons and scalar comparisons to guard relational behavior.  
RISKS: Private compute/reduce/containsMatch/findMatch behavior is reachable only through expression evaluation.  
RISKS: Context provides no concrete construction API or expected results beyond the reported empty-set failure.