TARGETS: JsonWriter.setLenient(boolean), value(double), and value(Number) handling of non-finite numbers.
ORACLES: Trigger expects lenient mode to permit NaN without IllegalArgumentException.
ORACLES: Non-lenient behavior is constrained by the reported exception message: numeric values must be finite.
CASES: setLenient(true) then write NaN via value(double); verify output/no exception as applicable.
CASES: setLenient(true) then write positive/negative infinity via value(double).
CASES: Exercise value(Number) with non-finite Double values if it follows the same validation path.
CASES: Compare finite double writing in default and lenient modes for regression safety.
RISKS: Exact serialized token/output is not stated in the supplied context.
RISKS: No other program version or broader JsonWriterTest source behavior is available.