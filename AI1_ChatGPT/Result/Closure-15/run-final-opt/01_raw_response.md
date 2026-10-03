TARGETS: FlowSensitiveInlineVariables.process, visit, GatherCandiates, Candidate.canInline/inlineVariable  
CASES: Simple for-in input exercised by FlowSensitiveInlineVariablesTest.testSimpleForIn  
ORACLES: Existing testSimpleForIn assertion is the available expected-result source  
CASES: Variable definition and use handling across for-in iteration flow  
RISKS: Incorrect inlining when for-in control flow changes reaching definitions or use counts  
RISKS: No method bodies, test input, or assertion details are provided in this context