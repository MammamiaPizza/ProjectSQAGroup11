TARGETS: JsonWriter.value(double) and value(Number) lenient/non-lenient paths.
ORACLES: No IllegalArgumentException when lenient=true for NaN/±Infinity; throw when false.
CASES: Normal doubles (0.0, ±1.0), Double.MIN/MAX, NaN, +Inf, −Inf with lenient true/false.
CASES: Write non-finite values at root, after name, inside array/object; verify writer state valid.
RISKS: Exact JSON text output for non-finite values is unspecified; only no-exception is known.
ORACLES: Based on bug report and testNonFiniteDoublesWhenLenient intent.
RISKS: Value may be output as string literal "NaN"/"Infinity"/"-Infinity" – format risk.
CASES: Mixed sequences: finite, non-finite, then finite again with and without lenient toggle.
RISKS: Flush/close after non-finite write may expose hidden state; ensure no NPE.