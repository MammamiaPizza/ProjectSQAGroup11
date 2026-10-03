TARGETS: FunctionInjector.isSupportedCallType classifies call as inlinable (YES/NO) based on args.
TARGETS: FunctionInjector.inlineReturnValue handles direct return and decompose-based inlining.
TARGETS: CanInlineResult enum decisions; prepare() sets up call-site-specific injection state.
ORACLES: testIssue1101a/b expects NO when args have side effects; bug returns YES for unsafe calls.
ORACLES: testInlineIfParametersModified8/9 expects no inlining when parameters are mutated in
condition.
ORACLES: testDoubleInlining2, testBug4944818, testInlineFunctions6 expect correct CanInlineResult.
CASES: Call with mutated argument (side-effect in arg) should be rejected.
CASES: Double inlining of same function when inlining already changes node identity.
CASES: Recursive function via fnRecursionName must not inline; modify-param branches must be safe.
RISKS: No code diff; expected behavior inferred from test names and assertion errors only.