TARGETS: TypedScopeCreator local declaration/type handling; LocalScopeBuilder.handleFunctionInputs/declareArguments.  
TARGETS: AbstractScopeBuilder.defineName/defineSlot behavior for duplicate local variables and function parameters.  
ORACLES: Existing trigger assertions: duplicate-local cases expect diagnostic count 2; TypeCheck expects no unexpected DUP_VAR warning.  
ORACLES: FunctionArguments13 and Scoping12 expect a warning; use existing test harness diagnostic assertions.  
CASES: Duplicate local `x` declarations with conflicting number/string types; verify diagnostics and retained scope typing.  
CASES: Function arguments scenario from FunctionArguments13; verify warning is emitted in TypeCheck and LooseTypeCheck.  
CASES: Scoping12 scope/parameter-local interaction; verify expected warning.  
RISKS: Target is package-private with mostly private methods; test through compiler/type-check test harness, not direct calls.  
RISKS: Context omits exact JavaScript fixtures and diagnostic types/messages; derive only from named existing tests.