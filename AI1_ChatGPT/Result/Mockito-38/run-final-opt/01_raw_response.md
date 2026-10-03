TARGETS: ArgumentMatchingTool.getSuspiciouslyNotMatchingArgsIndexes(List<Matcher>, Object[]).
TARGETS: Null-safe behavior in safelyMatches(Matcher,Object) and toStringEquals(Matcher,Object).
ORACLES: Existing trigger tests specify null arguments must not throw NullPointerException.
CASES: Matcher list with a null actual argument; verify suspicious-index result is produced safely.
CASES: Invocation/verification path passing null argument, as in ActualInvocationHasNullArgumentNPEBugTest.
CASES: Normal matching and nonmatching arguments to preserve suspicious-index behavior.
RISKS: Expected exact indexes for matcher/null combinations are not provided by this context.