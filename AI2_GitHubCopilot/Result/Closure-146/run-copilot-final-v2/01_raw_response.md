TARGETS: getTypesUnderEquality, testForEquality, restrictByNotNullOrUndefined, isVoidType
ORACLES: testEqCondition4 expects narrowed type "None" (NoType) not "undefined" (VoidType)
CASES: void/undefined equality narrowing; unions with void; shallow equality with void
RISKS: single failing test; other equality/void narrowing may be similarly broken but untested