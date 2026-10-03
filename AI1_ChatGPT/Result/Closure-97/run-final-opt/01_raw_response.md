TARGETS: PeepholeFoldConstants binary folding, especially private tryFoldShift for bit-shift expressions.
ORACLES: Existing PeepholeFoldConstantsTest::testFoldBitShifts assertions are the expected-result source.
CASES: Constant left/right operands for <<, >>, and >>>, including zero and ordinary positive shift counts.
CASES: Boundary shift counts relevant to JavaScript shift semantics; verify folded output matches test expectations.
RISKS: Folding must preserve JavaScript numeric/bitwise behavior and avoid incorrect constant conversion.
RISKS: Private methods require exercising through the optimization/test harness, not direct API calls.
RISKS: Context omits test source and AST/output helper APIs; do not assume assertion syntax or formatting.