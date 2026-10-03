TARGETS: Complex.reciprocal() for zero-valued complex inputs.
ORACLES: Trigger expects Complex.ZERO.reciprocal() to equal Complex.NaN, not Complex.INF.
CASES: reciprocal of (0.0, 0.0); assert isNaN() and equality with Complex.NaN.
CASES: reciprocal of (-0.0, 0.0) and (0.0, -0.0) as zero-boundary variants.
CASES: reciprocal of finite nonzero values to guard normal behavior without inventing values.
RISKS: Available context specifies only the zero reciprocal oracle; nonzero expected values are not provided.