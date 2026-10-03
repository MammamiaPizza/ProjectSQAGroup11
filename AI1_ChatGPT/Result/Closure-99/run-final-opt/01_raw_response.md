TARGETS: CheckGlobalThis.shouldTraverse, visit, shouldReportThis, and getFunctionJsDocInfo behavior.  
ORACLES: Existing trigger assertions and JSC_USED_GLOBAL_THIS diagnostic counts/messages.  
CASES: Property access of a method should produce exactly one global-this warning.  
CASES: Method4 pattern must not report JSC_USED_GLOBAL_THIS.  
CASES: Interface1 pattern must not report JSC_USED_GLOBAL_THIS.  
RISKS: Private decision logic is exercised through compiler traversal, not direct API calls.  
RISKS: Context omits source snippets/AST shapes; derive inputs only from existing tests.