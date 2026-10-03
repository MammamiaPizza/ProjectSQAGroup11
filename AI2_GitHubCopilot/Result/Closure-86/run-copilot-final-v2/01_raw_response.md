TARGETS: NodeUtil.evaluatesToLocalValue(Node, Predicate<Node>) — handles NEW nodes incorrectly
ORACLES: Test assertions expect NEW expr results excluded from side-effect sets (testIssue303,
testIssue303b)
CASES: Normal: NEW call with unmodified constructor; Boundary: NEW with local var argument; Error:
nested NEW calls
RISKS: Constructor side-effects from externs must still be detected; NEW may need 1-arg overload
compatibility