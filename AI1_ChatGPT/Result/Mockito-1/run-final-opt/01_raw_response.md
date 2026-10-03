TARGETS: InvocationMatcher.captureArgumentsFrom, matches, safelyArgumentsMatch, vararg helpers, createFrom  
ORACLES: InvocationMatcherTest should_capture_arguments_when_args_count_does_NOT_match  
ORACLES: Triggered varargs, stubbing, verification, and reflection tests define expected outcomes  
CASES: Capture matchers when invocation and matcher argument counts differ; no UnsupportedOperationException  
CASES: Null, empty, object, boolean, string, and mixed vararg arrays in matching/verification/stubbing  
CASES: Vararg matching with any-object/equality matchers and repeated/latest stubbings  
RISKS: Changed class is InvocationMatcher; reflection triggers may be indirect regression coverage  
RISKS: Context omits implementation and full assertions; derive expectations only from named tests