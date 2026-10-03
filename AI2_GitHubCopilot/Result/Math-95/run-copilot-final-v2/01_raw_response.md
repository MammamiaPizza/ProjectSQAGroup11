TARGETS: cumulativeProbability, inverseCumulativeProbability, getInitialDomain, getDomainLowerBound
with tiny numerator/denominator df
ORACLES: cumulativeProbability(0) == 0; cumulativeProbability is monotonic;
inverseCumulativeProbability(p) == x  ⇔ cumulativeProbability(x) ≈ p
ORACLES: getDomainLowerBound returns 0; getInitialDomain(p) >= 0 for all valid p
CASES: df (1e-4, 1e-4): p=0, p=1e-6, p=0.5, p=1-1e-6 for inverse; x=0, x>0 for cumulative;
getInitialDomain called with those p
CASES: Very small df combinations (1e-8, 1e-8) to stress domain initialization
CASES: Boundary p=0 and p=1 escape from inverse via try-catch for MathException
RISKS: Only buggy version available; expected values rely on mathematical properties, not reference
output
RISKS: Floating-point tolerance required for inverse check; getInitialDomain may still be buggy –
test that it is non-negative