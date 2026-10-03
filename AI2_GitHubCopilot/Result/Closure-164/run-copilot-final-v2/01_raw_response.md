TARGETS: ArrowType.getLeastSupertype, getGreatestSubtype, isSubtype, isEquivalentTo; check
function-type toString accuracy.
ORACLES: Trigger test expects getLeastSupertype of two ArrowType(number,number)->boolean returns
"function (number, number): boolean".
CASES: Same param count, different return types; different param counts; boundary: 0 params, void
return; error: null params or return.
RISKS: Only one expected output known; no bug report details; cannot run tests; inference of
supertype rules may be incomplete.