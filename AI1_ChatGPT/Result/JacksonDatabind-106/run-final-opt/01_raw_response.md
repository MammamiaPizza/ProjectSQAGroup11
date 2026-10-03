TARGETS: TreeTraversingParser.getIntValue() numeric conversion and overflow handling.
TARGETS: TreeTraversingParser.getLongValue() numeric conversion and overflow handling.
ORACLES: Trigger tests require failure when numeric values overflow int or long range.
CASES: In-range integer values return exact int/long values through parser traversal.
CASES: Test int minimum/maximum and values just outside each boundary.
CASES: Test long minimum/maximum and values just outside each boundary.
RISKS: NumericNode subtype/value construction is not provided; use only available project node APIs.
RISKS: Exception type/message is unspecified; assert failure only as established by trigger behavior.