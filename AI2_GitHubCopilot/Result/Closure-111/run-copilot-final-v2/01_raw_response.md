TARGETS: caseObjectType handling of Array check for goog.isArray outcome.
ORACLES: testGoogIsArray2 expects Array when goog.isArray=true.
CASES: goog.isArray true/false with object, array, null, undefined, top, all types.
RISKS: Only test-relevant methods are caseObjectType, apply; full internal state unknown.

TARGETS: apply(TypeRestriction) for outcome=true with array-like object.
ORACLES: Use JSType.ARRAY_TYPE equality as expected narrowed type.
CASES: Subtypes of Array (e.g., arguments, NodeList) true->Array, false->not array.
RISKS: Package mismatch (jscomp.type vs jscomp test); may need protected/package-private access.