TARGETS: ScopedAliases forward JSDoc alias/type rewriting during process; Scope Var metadata lookup may support it.  
ORACLES: Existing ScopedAliasesTest.testForwardJsDoc expected compiler output/source-position assertion.  
CASES: Alias declared before JSDoc use; ensure qualified type/name is rewritten to alias target.  
CASES: Forward JSDoc reference before alias declaration, the trigger scenario.  
CASES: Multiple aliases/usages and nested scope calls, preserving declaration-order handling.  
RISKS: ScopedAliases internals are private/package-private; test via compiler pass/output, not direct APIs.  
RISKS: Failure excerpt is truncated; exact expected transformed text/diagnostics are unavailable.