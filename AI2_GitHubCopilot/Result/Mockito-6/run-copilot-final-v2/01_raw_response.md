TARGETS: Matchers.anyBoolean/Byte/Char/Short/Int/Long/Float/Double;
anyString/List/Set/Map/Collection
ORACLES: assertNull after stub-return match with null argument; log unexpected non-null
CASES: null arg for each primitive-wrapper anyX → expect null, not 0 or false
CASES: null arg for anyString/List/Set/Map/Collection → expect null, not "matched"
CASES: non-null valid-type arg for same matchers → stub returns expected non-null value
RISKS: list from failures only; anyObject/any/anyVararg/any(Class) null-rejection untested
RISKS: null might still match incorrectly if matcher returns default but test environment differs
RISKS: JUnit 4 only; Hamcrest matchers version may influence assertion behavior