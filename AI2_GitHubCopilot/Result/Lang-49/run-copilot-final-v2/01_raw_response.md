TARGETS: Fraction.reduce(), Fraction.getReducedFraction(int,int), greatestCommonDivisor,
Fraction(int,int) constructor
ORACLES: gcd(reducedNum,reducedDen)==1; cross-multiply equality with original; mathematical
reduction rules
CASES: 100/100->1/1, 50/100->1/2, -1/2, denom=1, zero numerator (0/5), large shared factor,
irreducible 5/7
RISKS: Bug likely in greatestCommonDivisor when numerator==denominator or sign; constructor may not
normalize; reduce may skip non-trivial GCD