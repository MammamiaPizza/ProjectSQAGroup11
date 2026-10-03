TARGETS: Fraction(double,int) constructor with maxDenominator; BigFraction(double,int) digit-limit
constructor.
ORACLES: Mathematical best reduced rational approximation with denominator <= maxDenominator; no
overflow for exactly representable values.
CASES: Normal: 0.5 max=1000 -> 1/2; 0.333 max=10 -> 1/3; 0.0 max=any -> 0/1; 1.0 max=5 -> 1/1.
CASES: Boundary: maxDen=1 -> 0.5 rounds to 1/1?; Integer.MAX_VALUE; -0.5 -> -1/2; very small 1e-10
with large but safe maxDen.
CASES: Error: must not throw Overflow for simple representable values (0.5, 0.25, 0.2); ensure safe
intermediate arithmetic.
CASES: Error: values requiring denom > maxDen should throw FractionConversionException (e.g.,
0.123456789 max=2).
RISKS: Overflow from int/long range in continued fraction expansion; BigFraction should use
BigInteger but may still throw.
RISKS: Private constructor with epsilon/maxIterations may also be indirectly affected; focus public
maxDenominator constructor.