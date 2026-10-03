TARGETS: shouldReportThis(Node,Node) - core decision to flag global 'this'
TARGETS: getFunctionJsDocInfo(Node) - retrieves JSDoc to identify constructors/interfaces
ORACLES: Expected error counts from test assertions; JSC_USED_GLOBAL_THIS warning
ORACLES: testPropertyOfMethod expects 1 error; testMethod4 & testInterface1 expect 0
CASES: global 'this' in plain function (report), method prop assignment (bound? no report)
CASES: @interface methods (this should be interface instance, not global)
CASES: nested functions; this in function assigned to a property but called standalone
RISKS: Only method signatures provided, no source; JSDocInfo structure unknown
RISKS: Test semantics inferred from names; unknown visibility of internal helper logic