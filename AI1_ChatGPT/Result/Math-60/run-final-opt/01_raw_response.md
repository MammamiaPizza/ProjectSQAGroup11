TARGETS: NormalDistributionImpl.cumulativeProbability(double), especially extreme finite and infinite x.
ORACLES: Existing NormalDistributionTest::testExtremeValues and its no-ConvergenceException expectation.
CASES: x=Double.MAX_VALUE, -Double.MAX_VALUE, +Infinity, -Infinity; extreme mean/sd arithmetic.
CASES: Ordinary finite x to guard normal CDF behavior; verify no NaN/divergent continued fraction.
RISKS: No test source/body or patch is provided; exact numeric assertions beyond trigger are unavailable.