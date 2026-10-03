TARGETS: Fraction.add/subtract(Fraction), especially large numerators/denominators and overflow-safe reduction.
ORACLES: Trigger expects 2.1691754E9, not wrapped -2.125791896E9, from FractionTest::testMath835.
CASES: Reproduce testMath835 operands; assert resulting numerator/denominator and doubleValue().
CASES: Large coprime/cross-cancelable operands whose intermediate sum exceeds int but reduced result fits.
CASES: Add and subtract with same denominator, shared factors, opposite signs, and zero result.
CASES: Boundary values near Integer.MAX_VALUE/MIN_VALUE where valid reduced results remain representable.
RISKS: Exact trigger operand values and intended exception behavior are not provided in this context.