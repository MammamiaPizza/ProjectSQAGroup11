TARGETS: CoreOperationCompare.contains, findMatch, equal; comparison used by variable expression `$d = 'a'`.
ORACLES: Existing VariableTest::testIterateVariable expects Evaluating <$d = 'a'> to be true.
CASES: Variable iterator containing "a" versus scalar "a"; verify comparison returns true.
CASES: Iterator without matching value; empty iterator; matching value at first/later position.
CASES: Compare iterator-to-iterator behavior through findMatch, including one shared value and no shared values.
RISKS: Equality/coercion semantics are not provided; avoid asserting unshown null, numeric, or type-conversion behavior.
RISKS: Context exposes protected helpers but no concrete comparison subclass/API for direct invocation.