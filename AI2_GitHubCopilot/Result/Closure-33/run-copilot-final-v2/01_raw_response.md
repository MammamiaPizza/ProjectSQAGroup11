TARGETS: getSlot, hasProperty, hasOwnProperty, isPropertyTypeDeclared, isPropertyInExterns,
getImplicitPrototype
ORACLES: TypeCheckTest#testIssue700 expects zero warnings; property types match declared JSDoc in
prototype chain
ORACLES: Prototype-wired properties appear in getOwnPropertyNames counts and type resolution
CASES: Property on implicit prototype, not own; property on Object.prototype (e.g., toString)
CASES: Own property shadows prototype property; hasOwnProperty on inherited name returns false
CASES: Null implicit prototype; native-object inherit flag; property declared in externs vs inferred
RISKS: Exact trigger code unknown; test failure signals missing/extra type warning, not API contract
change
RISKS: Only PrototypeObjectType methods visible; no access to JSTypeRegistry or full JSCompiler
wiring