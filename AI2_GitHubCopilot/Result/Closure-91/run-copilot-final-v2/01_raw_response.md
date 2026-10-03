TARGETS: CheckGlobalThis.shouldReportThis(Node n, Node parent) — missing @lends check causing false
JSC_USED_GLOBAL_THIS.
TARGETS: getFunctionJsDocInfo(Node n) — retrieves JSDoc; used to see if function has @lends
annotation.
ORACLES: If function’s JSDoc contains @lends, shouldReportThis() must return false (no warning).
ORACLES: Verified by testLendsAnnotation3 expecting 0 errors; buggy version returns 1 error.
CASES: Normal: function with @lends {Type} and this usage → no warning.
CASES: Boundary: function without @lends still warns; @lends on non-function node has no effect.
CASES: Edge: @lends on function assigned to a prototype property via Object.defineProperty.
RISKS: Fix might only handle direct @lends on function expression; indirect or inherited annotations
may be missed.
RISKS: Over-suppression could hide genuine global this misuse inside functions with @lends but no
actual object.