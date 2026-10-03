TARGETS: SubTypeValidator.validateSubType(ctxt,type); SubTypeValidator.instance();
DEFAULT_NO_DESER_CLASS_NAMES
ORACLES: validateSubType for a blocked c3p0 class must throw exception with message "Illegal type"
ORACLES: validateSubType for a non-blocked class (e.g., String) must not throw that exception
ORACLES: SubTypeValidator.instance() returns a singleton (same on multiple calls)
CASES: direct call with type com.mchange.v2.c3p0.jacksontest.ComboPooledDataSource → expects
"Illegal type" exception
CASES: subclass of blocked superclass should be blocked (validateSubType traverses hierarchy)
CASES: calling with type of a safe class (e.g., java.lang.String) → no exception
CASES: boundary: validateSubType with a type whose raw class is Object.class → should not block
RISKS: In buggy version DEFAULT_NO_DESER_CLASS_NAMES may not contain c3p0, so the exception is not
thrown