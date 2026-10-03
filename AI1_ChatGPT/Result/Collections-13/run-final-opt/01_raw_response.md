TARGETS: ExtendedProperties.addProperty/load handling of multi-valued properties (COLLECTIONS-299 trigger).
ORACLES: Existing TestExtendedProperties.testCollections299 defines the regression expectation; failure must avoid NPE.
CASES: Add/load repeated key values; retrieve via getStringArray, getVector, and getList.
CASES: Single value then additional value; additional value then retrieval preserves accessible collection values.
RISKS: Exact input and expected ordering from trigger source are unavailable in this context.
RISKS: Do not infer behavior for interpolation, includes, conversion, or unrelated typed getters.