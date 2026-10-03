TARGETS: Compiler.getErrorCount() returns correct total across multiple inputs with ES5 strict mode
TARGETS: Compiler.compile() error aggregation logic; verify no reset or underflow to -1
TARGETS: Compiler.setErrorManager() initializes error tracking properly before multi-input compile
ORACLES: testES5StrictUseStrictMultipleInputs asserts error count == 17 (not -1)
ORACLES: Compare getErrorCount() with length of getErrors() array for consistency
CASES: Normal: 2–5 inputs each with distinct strict-mode violations → sum of per-input errors
CASES: Boundary: 0 inputs (empty list), 1 input, very many (100+) inputs with violations
CASES: Edge: inputs with zero strict violations → expected error count 0
CASES: Error: missing or unset ErrorManager before getErrorCount() → should return 0 or proper init
RISKS: Only known behavior is the test expectation; cannot infer true spec for error count semantics