TARGETS: CollapseProperties.process, inlineAliases, inlineAliasIfPossible, flattenReferencesTo,
collapseDeclarationOfNameAndDescendants
ORACLES: Compiler errors/warnings count, AST/JS output comparison (e.g., expected optimized code),
no unintended redefinitions
CASES: Collapsing namespace aliases (e.g., var ns = some.ns; ns.x); chained aliases; alias used as
LHS of property assignment; alias inside object literal; alias with same-name local variable
RISKS: No access to bug-931 diff; must infer correct collapse via public API only; risk of
over-collapsing or missing namespace warnings