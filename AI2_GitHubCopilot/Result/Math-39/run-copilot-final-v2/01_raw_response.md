TARGETS: integrate() initial step-size clamping before acceptance
TARGETS: apply setSafety, setMaxGrowth, setMinReduction to first-step guess
ORACLES: first accepted step size ≤ max allowed step (from t end, events, bounds)
ORACLES: step size reduced by factor ≤ maxGrowth, ≥ minReduction relative to rejected step
CASES: step=10×maxForward; step=1000×maxBackward; step exactly at max; step ≤0 (invalid)
CASES: maxGrowth=1.0; minReduction=0.99; safety very small (near 0.0)
RISKS: oracle needs internal stepSize via step handler or interpolator; tentative steps may differ
RISKS: only first-step behavior verified; later steps could still overshoot
RISKS: no formal spec; expected behavior deduced from testTooLargeFirstStep alone