TARGETS: FunctionBuilder.build return-type defaults and inferred-return handling.  
TARGETS: FunctionType.getReturnType/isReturnTypeInferred; TypedScopeCreator function declaration typing.  
TARGETS: FunctionTypeBuilder JSDoc-driven constructor/interface and function-type creation.  
ORACLES: Triggered CodePrinter tests compare emitted JSDoc/type annotation text.  
ORACLES: Triggered TypeCheck/LooseTypeCheck and TypedScopeCreator tests assert inferred function types.  
CASES: Functions without explicit return: expected undefined rather than unknown in displayed types.  
CASES: Constructor, method, nested function, optional/varargs, interface and prototype declarations.  
CASES: Explicit return types and inferred returns must remain distinct from default returns.  
RISKS: Context lacks method bodies, diffs, and full expected strings; avoid assuming non-listed APIs.