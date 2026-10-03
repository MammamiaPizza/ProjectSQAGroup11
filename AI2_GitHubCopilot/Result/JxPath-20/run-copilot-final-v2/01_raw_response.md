TARGETS: CoreOperationRelationalExpression.computeValue(), compute(), reduce(), findMatch()
ORACLES: Boolean result of relational expression; evaluateCompare(int) decides direction
CASES: Numeric mixed-type comparison, iterator-to-iterator matching, NaN/Infinity handling
RISKS: Bug likely in compute() or findMatch() logic for multi-step expressions like $a+$b<=$c