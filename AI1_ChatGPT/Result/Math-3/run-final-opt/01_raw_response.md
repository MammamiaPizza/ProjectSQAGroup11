TARGETS: MathArrays.linearCombination(double[] a, double[] b), especially one-element inputs.
ORACLES: Mathematical dot product; single pair result equals a[0] * b[0].
CASES: a={2}, b={3}: returns 6.0 and does not throw ArrayIndexOutOfBoundsException.
CASES: Single-element zero, negative, and fractional operand pairs.
CASES: Multi-element arrays retain dot-product behavior; boundary length is 1.
RISKS: Array-length mismatch behavior is not specified in provided context.
RISKS: Empty-array behavior and numerical precision expectations are not specified.