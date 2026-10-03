TARGETS: FunctionInjector call-site classification and can-inline decisions for supported call forms.  
TARGETS: Inlining must reject unsafe parameter modification/reference scenarios (Issue 1101).  
ORACLES: Existing FunctionInjectorTest expects NO for testIssue1101a and testIssue1101b.  
ORACLES: Existing InlineFunctionsTest output/assertions define valid transformed JavaScript.  
CASES: Calls whose parameters are modified; verify no inlining when aliases/evaluation may change.  
CASES: Double inlining, return-value inlining, and supported call types exercised by listed triggers.  
CASES: Normal safe calls versus unsafe calls with parameter mutation and repeated references.  
RISKS: APIs are mostly private; tests likely require compiler AST/test harness rather than direct calls.  
RISKS: Failure summaries omit exact source snippets and expected transformed code.