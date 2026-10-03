TARGETS: NormalDistributionImpl.cumulativeProbability(double), inverseCumulativeProbability(double)
ORACLES: MathException/domain checks, convergence within 10k iterations, symmetric results,
monotonic probabilities
CASES: p near 0/1 boundary, extreme x values (large negative/positive), mean=0 sd=1, mean shifted,
sd varied
RISKS: MaxIterationsExceededException on extreme values, convergence failure for tail probabilities