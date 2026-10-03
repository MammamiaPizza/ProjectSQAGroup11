TARGETS: getRMS(), getChiSquare(), updateResidualsAndCost(), guessParametersErrors()
TARGETS: updateJacobian() applies -sqrt(weight) scaling, which alters optimization path.
ORACLES: testCircleFitting asserts optimizer.getRMS() == 0.004; known reference from
LevenbergMarquardt.
ORACLES: Chi-square = sum(weight_i * residual_i^2); RMS = sqrt(chiSquare/(rows-cols)) per typical
formula.
CASES: Normal: circle fit with 10-100 noisy points; boundary: zero weights cause NaN in Jacobian.
CASES: Error: singular Jacobian leads to cost spikes; max iterations reached; negative weight breaks
math.
RISKS: Exact test code absent; bug likely in weight handling or RMS denominator (rows vs rows-cols).