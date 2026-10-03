TARGETS: FastMath.pow(double,double) with negative base and odd integer exponent yields wrong sign
(1.0 instead of -1.0).
TARGETS: FastMath.pow(double,int) overload likely shares the same sign error for negative base/odd
exponent.
ORACLES: Expected sign per Math contract: negative base with odd integer exponent => negative; use
StrictMath.pow or manual integer-power as reference.
ORACLES: Non-integer exponent on negative base => NaN; even integer exponent => positive;
pow(0,negative) => Infinity with proper sign.
CASES: Normal: base=-1,exp=1 (trigger MATH-904), -2^3 (expect -8), -3^2 (expect 9), -5^0 (expect 1),
-10^-1 (expect -0.1 sign).
CASES: Boundary: base=-0.0, base=Double.MIN_VALUE negative with odd/even small exp, exp=0, exp=-1,
exp=Double.MAX_VALUE odd.
CASES: Error: negative base with non-integer exp => NaN, pow(0,negative) => ±Infinity, pow(±Inf,exp)
overflow/underflow sign.
RISKS: Only pow changed; other FastMath methods (hypot, exp, log) do not call pow, but user code
relying on pow is affected.
RISKS: Bug inferred from -1.0 vs 1.0 failure only; must cover all integer exponent detection
branches and sign handling in pow implementation.
RISKS: Implicit sign error may stem from fast-exp algorithm miscomputing sign for negative base;
verify exact integer detection logic.