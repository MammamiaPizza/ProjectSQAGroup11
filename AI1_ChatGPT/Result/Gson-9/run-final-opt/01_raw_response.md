TARGETS: JsonWriter.value(Boolean), JsonTreeWriter.value(Boolean), and TypeAdapters.BOOLEAN.write null/boxed values.
ORACLES: JsonWriterTest::testBoxedBooleans is the only stated expected-result source; failure must avoid NPE.
CASES: Write Boolean.TRUE and Boolean.FALSE through JsonWriter; verify normal boolean JSON output/state.
CASES: Write null boxed Boolean via TypeAdapters.BOOLEAN and writer/tree writer; verify null handling without NPE.
CASES: Mix boxed booleans in arrays/objects, including null, to cover nesting and name/value state transitions.
RISKS: Exact serialized formatting and null policy are not provided; derive assertions only from existing test context/API.