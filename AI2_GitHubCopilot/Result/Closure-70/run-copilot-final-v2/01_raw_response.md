TARGETS: TypedScopeCreator.createScope, LocalScopeBuilder.visit, handleFunctionInputs,
declareArguments
ORACLES: Count of JSC_DUP_VAR_DECLARATION warnings; presence/absence of such warning per test code
CASES: Duplicate local var with number then string; function argument x shadowed by local var x with
diff type; block-level redeclaration; multiple same-name vars in same scope
RISKS: Only API signatures given; cannot infer exact expected warning counts; compiler options may
affect behavior