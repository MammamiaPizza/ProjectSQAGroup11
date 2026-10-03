TARGETS: ReferenceCollectingCallback.visit, enterScope, exitScope, addReference, and block-boundary handling.  
TARGETS: ReferenceCollection assignment/initialization queries used by InlineVariables.  
ORACLES: Existing InlineVariablesTest.testExternalIssue1053 assertion/failure outcome.  
CASES: References across scopes and BasicBlock boundaries, including declarations and assignments.  
CASES: Normal symbol lookup plus references involving initializing declarations/assignments.  
RISKS: No source diff or trigger test body/expected assertion is provided; exact regression input is unavailable.