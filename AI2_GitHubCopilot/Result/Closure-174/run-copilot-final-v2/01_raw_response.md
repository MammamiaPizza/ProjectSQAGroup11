TARGETS: ScopedAliases.validateScopeCall: detecting non-alias locals incorrectly triggers
JSC_GOOG_SCOPE_NON_ALIAS_LOCAL
TARGETS: ScopedAliases.Traversal.visit: handling of VAR nodes inside goog.scope;
NodeUtil.isNameOrGetProp for alias detection
TARGETS: JsAst.getAstRoot/parse: parsing of JS with goog.scope to produce correct AST nodes for
alias analysis
ORACLES: compiler should not emit JSC_GOOG_SCOPE_NON_ALIAS_LOCAL for non-alias var inside
goog.scope; expected 0 errors
CASES: goog.scope with goog.provide and only aliases → 0 errors; with plain var a=10; → 0 errors
(after fix)
CASES: goog.scope with both alias (a=goog.require(...)) and non-alias local var b=5 → verify no
false positive
CASES: nested goog.scope, function inside scope with alias, or multiple goog.scope calls → no
internal compiler error
RISKS: need full Compiler harness to invoke ScopedAliases pass; limited insight into exact NodeUtil
changes for false-positive fix
RISKS: without access to testIssue1103a sample input, reproducing the exact AST structure that
triggers the bug may be difficult
RISKS: error reporting mechanism (JSError, WarningLevel) may require specific Compiler flags or
error manager setup for assertion