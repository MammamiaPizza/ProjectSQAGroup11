TARGETS: CheckGlobalThis.shouldTraverse and visit reporting of global `this` via traversal  
ORACLES: Existing trigger expects 0 JSC_USED_GLOBAL_THIS errors for lends annotation input  
CASES: Function/object code with `@lends` annotation containing `this` at line 1  
CASES: Compare ordinary dangerous global `this` reporting versus annotation-exempt traversal  
RISKS: Private shouldReportThis/getFunctionJsDocInfo require indirect compiler-based tests  
RISKS: Context provides only one failing annotation case; no broader expected semantics supplied