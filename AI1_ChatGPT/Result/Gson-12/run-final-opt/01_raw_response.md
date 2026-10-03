TARGETS: JsonTreeReader.skipValue() while positioned at object names and object values.
ORACLES: Trigger tests specify no ArrayIndexOutOfBoundsException for filled and empty JsonObject skipping.
CASES: Skip a filled JsonObject; verify reader stack/path remains usable after the skip.
CASES: Skip an empty JsonObject; verify completion without stack underflow/index -1.
RISKS: Context lacks test bodies and intended post-skip assertions beyond trigger failures.