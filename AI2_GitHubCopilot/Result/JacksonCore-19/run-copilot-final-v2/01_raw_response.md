TARGETS: _parseNumber() in both parsers—likely the source of ArrayIndexOutOfBoundsException on long
floats.
ORACLES: TestNumericValues::testLongerFloatingPoint triggers; expect no exception; validate
JsonToken and getDoubleValue()/getBigDecimalValue().
CASES: Floating-point literals >=200 chars; boundary near internal buffer size; trailing zeros;
scientific notation with many digits; edge near Integer.MAX_VALUE digit count.
RISKS: No source diff provided; bug may be in shared number-parsing logic; buffer-expansion edge
cases; class is final in 2.3 so sub-classing not possible.