TARGETS: reduce(), getReducedFraction(int,int), greatestCommonDivisor(int,int)
ORACLES: Test expects numerator=-1073741824 for reduce(Integer.MIN_VALUE,2); expected GCD=2
CASES: (Integer.MIN_VALUE,2), (Integer.MIN_VALUE,-2), (Integer.MIN_VALUE,4),
(Integer.MAX_VALUE,Integer.MAX_VALUE)
CASES: Normal reduction (6,8)->3/4; zero numerator; denominator already positive; (x,1)
CASES: Fractions with large GCD but no overflow; negative denominators normalized to positive denom
RISKS: gcd overflow when one arg is Integer.MIN_VALUE yields 0 or 1, blocking correct reduction
RISKS: Bug may affect add/subtract/multiply/divide if they call reduce; only buggy version, no
source