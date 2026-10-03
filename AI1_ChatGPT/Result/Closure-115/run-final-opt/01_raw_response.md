TARGETS: FunctionInjector inlining decisions/call-site handling exercised by InlineFunctionsTest triggers.
TARGETS: setKnownConstants(Set<String>) may affect inlining assumptions; enum/inner methods are non-public.
ORACLES: Existing trigger assertions in InlineFunctionsTest are the only stated expected-result source.
CASES: Cover trigger scenarios: Bug4944818, double inlining, and InlineFunctions6.
CASES: Cover parameter-modification rejection scenarios from NoInlineIfParametersModified8 and 9.
RISKS: No source bodies, compiler-input strings, or expected transformed output are provided.
RISKS: Private methods and package-private class limit direct unit testing without existing test infrastructure.