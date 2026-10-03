TARGETS: getReducedFraction(int,int) and reduce() normalization with extreme negative numerator/denominator.
ORACLES: Trigger assertions require numerator -1073741824, not Integer.MIN_VALUE.
CASES: Reduce (-2147483648,2) to (-1073741824,1); verify numerator and denominator.
CASES: getReducedFraction(-2147483648,2) yields the same reduced fraction.
CASES: Verify equivalent sign-normalized input (2147483648 unavailable as int) is not testable.
RISKS: Context lacks implementation and full expected exception/overflow behavior; do not infer it.