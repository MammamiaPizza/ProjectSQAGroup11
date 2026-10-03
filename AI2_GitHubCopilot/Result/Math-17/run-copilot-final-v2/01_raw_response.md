TARGETS: Dfp.multiply(Dfp); multiply(int); multiplyFast(int); round(int); dotrap/trap flag handling
ORACLES: DfpTest::testMultiply assertions; DfpField decimal-floating semantics; invalid-op flag
codes
CASES: normal * normal; x*0/1/2; sign combos; overflow->INFINITE; underflow; NaN/Inf operand traps
CASES: case #37 reproduced: input pair that yields NaN with flag 1 instead of finite/exact result
RISKS: exact expected flags/rounding hidden; not given test source or buggy-vs-fixed diff
RISKS: flag code 1 meaning (user/assert mode) inferable only from Dfp context, not confirmed