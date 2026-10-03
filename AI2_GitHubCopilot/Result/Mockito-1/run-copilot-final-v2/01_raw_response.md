TARGETS: captureArgumentsFrom, matches, createFrom, hasSimilarMethod, getInvocation,
isVarargMatcher.
ORACLES: existing Mockito tests (should_capture_arguments_when_args_count_does_NOT_match; varargs
tests): expect no UnsupportedOperationException, args captured when count matches or varargs expand.
CASES: args count == matchers; args count < matchers (trailing vararg); args count > matchers
(vararg); null vararg; mixed varargs; overloaded same-name methods; anyVararg matchers.
RISKS: internal CapturesArguments/Matcher APIs not given; vararg detection logic unknown; must not
guess fixed behavior.