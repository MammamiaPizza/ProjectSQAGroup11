TARGETS: computeValue() in the 4 relational classes; CoreOperationRelationalExpression helper;
getSymbol().
ORACLES: testNodeSetOperations assertions; JXPath "any node satisfies relation" semantics; pairwise
scalar comparisons.
CASES: $array node-set > 0; empty set vs scalar; singleton matching/non-matching values; null
operands.
CASES: >= and <= at exactly-equal boundary; multi-node set with one true/one false; inverse of < and
>.
RISKS: only public signatures supplied; cannot retrieve current CoreOperation internals or docs.