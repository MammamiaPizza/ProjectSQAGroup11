TARGETS: FunctionTypeBuilder.build()/withInferredReturnType default to VOID_TYPE, not UNKNOWN_TYPE.
TARGETS: FunctionType.getReturnType().toString() should be "undefined" when no return info.
ORACLES: Expected "undefined" string from type printer; compare with
registry.getNativeType(VOID_TYPE).
ORACLES: Verify isReturnTypeInferred() is false when no explicit return annotation.
CASES: Normal: function f(){} -> return type undefined; function with explicit @return {?} -> still
?.
CASES: Boundary: constructor without @return -> return type undefined (not ?).
CASES: Error: @extends without @constructor may lead to unexpected unknown return if not handled.
RISKS: Fix may be localized to FunctionTypeBuilder but integration with TypedScopeCreator could mask
regression.
RISKS: Insufficient context to confirm if setting default to undefined for all functions is correct
(e.g., externs?).