TARGETS: ScopedAliases.process/hotSwapScript traversal of goog.scope alias and local-variable validation.
TARGETS: ScopedAliases.validateScopeCall, findAliases, namespace-shadow renaming, alias usage application.
TARGETS: JsAst.getAstRoot/parse and NodeUtil AST classification used during scoped-alias traversal.
ORACLES: Existing trigger tests require zero JSC_GOOG_SCOPE_NON_ALIAS_LOCAL diagnostics for issue-1103 inputs.
ORACLES: Existing trigger test 1103b requires compilation to complete without INTERNAL COMPILER ERROR.
CASES: Compile each issue-1103a/b/c source through ScopedAliases and assert diagnostics/exception outcome.
CASES: Cover locals named a in goog.scope at the trigger positions, including alias-reference ordering if present.
RISKS: Exact JavaScript inputs and intended transformed AST/output are not provided in this context.
RISKS: Do not infer behavior beyond trigger diagnostics and absence of compiler crash.