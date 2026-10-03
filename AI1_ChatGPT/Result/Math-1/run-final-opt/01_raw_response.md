TARGETS: BigFraction(double,int) and Fraction(double,int) digit/maximum-denominator conversion constructors.  
ORACLES: Trigger expectation: converting 0.5 must yield 1/2, not overflow with huge convergents.  
CASES: Both classes: new *(0.5, 1) and new *(0.5, 2) should represent numerator 1, denominator 2.  
CASES: Boundary maxDenominator below 2 may select an allowed approximation; verify only documented constructor outcome.  
RISKS: Continued-fraction overflow/order near exact values; test both BigInteger and int-backed implementations.  
RISKS: Available context omits full constructor semantics and accessor signatures; avoid unsupported assertions.