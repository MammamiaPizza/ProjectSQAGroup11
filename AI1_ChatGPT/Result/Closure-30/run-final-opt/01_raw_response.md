TARGETS: FlowSensitiveInlineVariables.process/visit and Candidate.canInline/inlineVariable flow-sensitive substitutions.
TARGETS: MustBeReachingVariableDef analysis, MustDef join, dependency tracking, and parameter escaping.
ORACLES: Existing three triggering FlowSensitiveInlineVariablesTest assertions and compiler output expectations.
CASES: Inline across a side-effecting statement; ensure reaching definition/use safety is preserved.
CASES: Inline across a no-side-effect statement when the definition remains uniquely reaching.
CASES: Issue698 scenario covering affected flow/dependency behavior.
RISKS: No source bodies or expected transformed JavaScript provided; derive inputs/outputs only from existing tests.