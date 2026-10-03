TARGETS: ExtendedProperties interpolation: interpolate/interpolateHelper, ${} tokens,
escape/unescape, priorVariables recursion guard.
TARGETS: addProperty/setProperty/getProperty/put/load/save/combine/clearProperty;
getStringArray/getList/getVector; typed getters.
ORACLES: Assertions in TestExtendedProperties.testCollections271 (body not supplied);
ExtendedProperties Javadoc/src comments; save+load round-trip.
CASES: Self-referential/cyclic ${} interpolation; empty, null, missing, and whitespace-only
keys/values.
CASES: Boundary no-delimiter vs multi-value in getStringArray/getList; escaped
delimiters/backslashes and comment/equals chars on load.
CASES: scalar+multi-value combine/putAll interaction; default-value vs missing-key behavior in
getString/getInt/getBoolean.
RISKS: Failing test body and fixed diff unavailable; cannot confirm exact COLLECTIONS-271 contract
or delimiter/escape semantics.
RISKS: Do not assert undocumented defaults or APIs without executing the supplied trigger; avoid
relying on other program versions.