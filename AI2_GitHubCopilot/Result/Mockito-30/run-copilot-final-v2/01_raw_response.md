TARGETS: ReturnsSmartNulls.ThrowingInterceptor.intercept(), formatMethodCall(); Reporter method that
constructs smart-null exception message.
ORACLES: Exception message must contain argument names/values (e.g., "oompa","lumpa") as formatted
by formatMethodCall(); test assertion in shouldPrintTheParametersOnSmartNullPointerExceptionMessage.
CASES: Normal: 2-arg method with distinct values; empty args; null args; primitive args; long
argument strings; overloaded methods with parameter info.
RISKS: The exact Reporter smart-null method name/signature not in provided list; must infer from
ReturnsSmartNulls code; limited context without full project test files.