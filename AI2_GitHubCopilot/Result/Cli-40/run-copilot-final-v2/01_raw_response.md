TARGETS: TypeHandler.createValue(String, Class) for Integer.class
TARGETS: TypeHandler.createValue(String, Object) with Integer instance
ORACLES: Must throw ParseException for invalid integer strings, per failing test
testCreateValueInteger_failure
ORACLES: createNumber(String) wraps NumberFormatException in ParseException; reference for expected
behavior
CASES: null, "", "abc", "1.5", "2147483648" (overflow), "+100", " 42 ", "-100"
RISKS: Float/Double/other numeric types may also lack ParseException wrapping; test independently
RISKS: Whitespace handling might differ between parse methods; unclear specification
RISKS: Reliance on test expectation only; no explicit javadoc contract for exception behavior