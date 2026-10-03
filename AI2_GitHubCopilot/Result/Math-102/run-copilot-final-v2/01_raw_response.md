TARGETS: chiSquare(double[] expected, long[] observed) — computes test statistic for
goodness-of-fit.
TARGETS: chiSquare(long[][] counts) — computes test statistic for independence/contingency tables.
ORACLES: Expected statistic from MATH-175: 9.023307936427388 for normal data, 114875.90421929007 for
large data.
ORACLES: Expected values come from corrected formula per bug report; trust these values as ground
truth.
CASES: Normal expected/observed arrays matching typical usage; ensure delta comparison with expected
values.
CASES: Large expected/observed arrays (e.g., many categories, high counts) to trigger floating-point
precision or overflow issues.
CASES: 2×2 contingency table with balanced and unbalanced margins; test edge when observed =
expected (statistic 0).
CASES: Invalid inputs: negative observations, non-rectangular counts matrix; verify
IllegalArgumentException.
RISKS: Only ChiSquareTestImpl is modified; distribution factory and other classes stay unchanged in
this version.
RISKS: Test must not assume specific implementation details beyond the public API; rely solely on
expected statistic values.