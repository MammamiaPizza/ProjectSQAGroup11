TARGETS: FieldUtils.safeMultiply(long, int)
ORACLES: FieldUtils Javadoc – must return exact product or throw ArithmeticException on overflow
CASES: Normal range: small positives, negatives, zero
CASES: Boundary: multiply near Long.MAX_VALUE with ±2, Long.MIN_VALUE with ±2
CASES: Overflow: product > Long.MAX_VALUE (e.g., Long.MAX_VALUE * 3), product < Long.MIN_VALUE
CASES: Edge: multiply with Integer.MAX_VALUE, Integer.MIN_VALUE, -1
RISKS: No access to exact Javadoc or detecting thresholds; tests must avoid checking overflow window
that depends on implementation