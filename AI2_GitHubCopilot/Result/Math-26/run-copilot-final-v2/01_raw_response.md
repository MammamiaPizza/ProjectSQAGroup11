TARGETS: Fraction(int,int) constructor reduction overflow, multiply, divide, add, subtract.
ORACLES: ArithmeticException or FractionConversionException on overflow; otherwise proper sign.
CASES: num/den = Integer.MIN_VALUE, MIN_VALUE+1, -1; multiply MIN_VALUE by -1; add MAX_VALUE to
itself.
RISKS: Exact exception type unspecified; negative zero reduction possible; normalization may
overflow before check.