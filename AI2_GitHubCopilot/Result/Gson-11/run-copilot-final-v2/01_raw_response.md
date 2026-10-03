TARGETS: TypeAdapters.BYTE.read, SHORT.read, INTEGER.read, LONG.read, FLOAT.read, DOUBLE.read
methods for number-deserialization from string tokens.
TARGETS: The TypeAdapterFactory wrappers that delegate to these adapters for primitive/boxed number
types.
ORACLES: Expected: string token with numeric chars -> Number via parseXxx; non-numeric string ->
NumberFormatException; from bug #964 description.
ORACLES: Bug report: "JSON string containing a number should be deserialized as a number" for
numeric types.
CASES: Valid numeric strings: '"123"' -> 123 (Integer), '"3.14"' -> 3.14 (Float), '"1e2"' -> 100.0
(Double), etc.
CASES: Boundary: empty string, null, whitespace-only, leading/trailing spaces, large values,
negatives, scientific notation.
CASES: Error: '"abc"' -> NumberFormatException; '"123abc"' -> error; ensure proper token type
mismatch error is thrown.
CASES: Regression: regular JSON number token still parses correctly; writing still emits number (not
string) token.
RISKS: Locale-sensitive parsing (decimal separator); leading/trailing whitespace behavior ambiguous;
no fixed version for comparison.
RISKS: Expected exception type uncertain (NumberFormatException vs JsonParseException); overflow
handling may vary.