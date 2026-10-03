TARGETS: JsonWriter.value(Boolean) and TypeAdapters.BOOLEAN.write; JsonTreeWriter.value(boolean).
TARGETS: Overload resolution among value(boolean/long/double/Number) for boxed/null arguments.
ORACLES: JsonWriterTest.testBoxedBooleans expects JSON tokens true, false, and null for null
Boolean.
ORACLES: JSON/string output must be "null" when a boxed Boolean is null; no NPE.
CASES: value(Boolean.TRUE) -> true; value(Boolean.FALSE) -> false.
CASES: value((Boolean)null) and TypeAdapters.BOOLEAN.write(null) -> null with no exception.
CASES: Null Boolean after beginArray/name vs top-level value; null Number vs primitive overloads.
RISKS: API list truncates value(Boolean) and test body; behavior inferred from issue 836 NPE.
RISKS: JsonTreeWriter and TypeAdapters edits may change adapter path separately from direct
JsonWriter use.