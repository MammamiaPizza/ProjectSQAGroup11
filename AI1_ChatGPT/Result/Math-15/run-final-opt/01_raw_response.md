TARGETS: FastMath.copySign(double,double); likely sign-bit handling implicated by testMath904.
TARGETS: FastMath.copySign(float,float) is analogous and merits parity coverage.
ORACLES: Trigger specifies copy result expected -1.0 but actual 1.0 in testMath904.
CASES: copySign(1.0, -0.0) should preserve the negative sign and yield -1.0.
CASES: Cover positive/negative magnitudes with positive and negative nonzero signs.
CASES: Cover -0.0 magnitude and verify sign-bit-sensitive zero results.
RISKS: Numeric comparisons may hide signed-zero differences; inspect raw bits or reciprocal sign.
RISKS: NaN sign semantics are not provided here; avoid asserting them without local evidence.
RISKS: Modified-method details are truncated; scope beyond FastMath is unavailable.