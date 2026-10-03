TARGETS: primitiveValueOrNullFor must return boxed default matching primitive type (Double for
double, not Integer).
ORACLES: Existing test assertions (e.g., ReturnsEmptyValuesTest expects 0.0 for double, not 0).
CASES: All 8 primitive types; wrapper class input; null/invalid input; verify no ClassCastException
on returned values.
RISKS: Hard-coded primitiveValues map currently has int 0 for double.class causing cast failures;
fix must be isolated to map entries.
TARGETS: primitiveWrapperOf correctness for each wrapper type.