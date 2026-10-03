TARGETS: Fraction.compareTo(Fraction)
ORACLES: Comparable contract: equal-value fractions (even different num/den) must return 0; else
sign of cross-product diff.
CASES: 1/2 vs 2/4 (equal), 1/3 vs 1/2 (less), -1/2 vs 1/3 (less), 0 vs 0/1 (equal), unreduced like
6/8 vs 3/4.
CASES: negative denominators, int overflow large num*den, Integer.MIN_VALUE/ -1.
RISKS: Likely bug in cross-multiply ignoring reduction; only compareTo behavior known; no other
affected methods.