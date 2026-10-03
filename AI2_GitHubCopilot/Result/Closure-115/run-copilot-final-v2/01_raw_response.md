TARGETS: FunctionInjector.isSupportedCallType, inlineReturnValue, classifyCallSite
ORACLES: Expected inlining decisions from test name prefixes (e.g., should NOT inline when
parameters are modified)
CASES: functions where formal parameter is reassigned (arg modification), double nested inlining,
return-value inlining
RISKS: Only test-name-based oracle; actual expected behaviour is hidden in test assertions not
provided; no fixed version reference