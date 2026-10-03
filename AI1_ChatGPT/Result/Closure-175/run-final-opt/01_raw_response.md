TARGETS: FunctionInjector inlining eligibility/call-site classification and cost estimation behavior.  
ORACLES: Existing FunctionInjectorTest Issue1101 assertions expect CanInlineResult.NO, not YES.  
ORACLES: Existing InlineFunctionsTest assertions define compiler output/inlining decisions.  
CASES: Issue1101a/1101b call patterns that must be rejected for inlining.  
CASES: Cost-based inlining boundary in testCostBasedInlining10.  
CASES: Mutable arguments referenced once in testInlineMutableArgsReferencedOnce.  
RISKS: Available signatures omit public decision methods and exact AST/input-output expectations.