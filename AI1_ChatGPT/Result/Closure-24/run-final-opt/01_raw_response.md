TARGETS: ScopedAliases.process and hotSwapScript scope-call traversal/validation behavior.
ORACLES: Trigger expects exactly one compiler error for testNonAliasLocal.
CASES: A scope call containing a local name that is not a recognized alias.
CASES: Valid alias definitions/usages should remain accepted and rewritten consistently.
CASES: Boundary scope-call forms and nested/local scope handling during traversal.
RISKS: Error reporting may differ between full process and hotSwapScript paths.
RISKS: Context lacks source bodies, diagnostic type/message, and trigger input details.