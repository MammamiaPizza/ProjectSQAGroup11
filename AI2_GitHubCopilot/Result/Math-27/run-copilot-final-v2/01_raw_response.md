TARGETS: Fraction(double), Fraction(double,int), Fraction(double,double,int) constructors
ORACLES: Fraction sign matches input double sign; numerator/denominator signs consistent;
doubleValue() ≈ input within epsilon; fraction is reduced
CASES: Positive double just above Integer.MAX_VALUE (trigger value ~2.17e9), just below it; negative
large magnitudes; zero; small doubles; NaN/Inf (expect exception)
CASES: Boundary: Integer.MAX_VALUE, Integer.MIN_VALUE; negative double near -2.17e9; doubles with
many fractional digits
RISKS: No source code; internal overflow path unknown; epsilon/maxDenominator effects unverified;
behavior for NaN/Inf unspecified