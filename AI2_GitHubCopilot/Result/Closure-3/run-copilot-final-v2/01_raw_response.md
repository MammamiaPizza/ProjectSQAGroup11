TARGETS: Candidate.canInline, Candidate.getDefinition, MustDef analysis, inlineVariable
ORACLES: testDoNotInlineCatchExpression1,1a,3: expect variable not inlined when used in catch
CASES: Var defined before try, only use in catch; var defined in try used in catch; catch param
mask; nested catch; finally block use
RISKS: Only three trigger tests; may miss catch with conditional returns; only
FlowSensitiveInlineVariables covered