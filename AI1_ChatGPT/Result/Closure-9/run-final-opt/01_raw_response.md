TARGETS: ProcessCommonJSModules.toModuleName(String) filename-to-module-name conversion.
ORACLES: Trigger test expects "module$baz"; observed result was "module$foo\baz".
CASES: Reproduce testGuessModuleName filename normalization path yielding the trigger comparison.
CASES: Cover ordinary filename conversion and boundary path components implicated by the trigger.
RISKS: normalizeSourceName is private; available context does not expose its expected behavior.