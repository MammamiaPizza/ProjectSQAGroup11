TARGETS: apply(TypeRestriction), caseTopType, caseObjectType, caseFunctionType, caseAllType  
ORACLES: Existing test testGoogIsArray2 expects inferred type Array  
CASES: goog.isArray true-path refinement from top/unknown input to Array  
CASES: goog.isArray false-path should preserve/restrict consistently with TypeRestriction outcome  
CASES: object/function/all-type inputs through reverse interpreter cases  
RISKS: Context omits call-site recognition, TypeRestriction construction, and full expected false-path behavior