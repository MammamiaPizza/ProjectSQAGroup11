TARGETS: CSVRecord.toMap(), putIn(Map), and mapping-dependent access behavior.
ORACLES: Trigger test testToMapWithNoHeader; bug report CSV-118; API exception message for get(name).
CASES: Record without header mapping: toMap() should not throw NullPointerException.
CASES: Header-mapped record: toMap()/putIn should map names to corresponding values.
CASES: Mapping index beyond values: verify existing missing-value behavior through public methods.
RISKS: Constructor/setup API is not provided; tests must use only discoverable project-visible construction paths.
RISKS: No expected empty-map behavior is explicitly stated beyond avoiding the trigger NPE.