TARGETS: FastMath.pow(double,double) with extremely large exponents (|y| > 2^30).
ORACLES: Compare to java.lang.Math.pow; for x in (0,1) and large +y expect 0.0, for x>1 and large -y
expect 0.0; overflow → +Infinity.
CASES: pow(<1, hugePos) → 0.0; pow(>1, hugeNeg) → 0.0; pow(0, neg) → +Infinity; pow(0, pos) → 0.0;
pow(−1, +Inf); tiny bases, large exp magnitudes.
RISKS: Unknown internal overflow thresholds; intermediate exp/ln steps may lose accuracy; NaN for
0^0; subnormals may underflow inconsistently.