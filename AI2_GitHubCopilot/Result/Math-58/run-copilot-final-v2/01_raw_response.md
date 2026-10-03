TARGETS: GaussianFitter.fit(), ParameterGuesser.guess(), basicGuess().
TARGETS: GaussianFitter.gradient() computing partial derivatives of Gaussian.
ORACLES: Fitted sigma>0, amplitude≥0; initial guess from ParameterGuesser must be valid.
ORACLES: Gradient values match analytic derivatives of Gaussian.
CASES: Normal symmetric peak with noise; minimal (3 points); all points identical.
CASES: Boundary: monotonic increasing/no interior maximum; all same x.
CASES: Error: null observations, empty array, single point.
RISKS: Negative sigma arises from basicGuess() sorting/selection logic.
RISKS: Cannot inspect exact test data; only known trigger MATH-519 NotStrictlyPositive.