TARGETS: TypeInference traversal and call-site backward inference affecting TypeCheck warning emission.
ORACLES: Existing trigger TypeCheckTest.testIssue1056 expects a warning.
CASES: Reproduce issue 1056 source pattern through TypeCheck and assert warning is reported.
CASES: Include relevant call/parameter type flow that should yield a type mismatch warning.
RISKS: TypeInference methods are private; test via compiler/TypeCheck integration, not direct calls.
RISKS: Context lacks issue-1056 input and exact diagnostic type/message; do not assume either.