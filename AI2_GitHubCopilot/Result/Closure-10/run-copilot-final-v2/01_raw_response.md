TARGETS: mayBeString(Node), allResultsMatch(Node,Predicate), isNumericResult(Node),
getStringValue(Node)
ORACLES: testIssue821 in PeepholeFoldConstantsTest; JS semantics for AND/OR/COMMA string coercion
CASES: AND/OR/COMMA with non-literal string operands; ANY_TYPE nodes; mixed number+string results
RISKS: No fixed-version reference; mayBeString predicate logic opaque; Node type hierarchy not fully
visible