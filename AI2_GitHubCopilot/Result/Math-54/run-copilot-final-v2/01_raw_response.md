TARGETS: divide(Dfp), sqrt(), subtract(Dfp), add(Dfp), round(int), trunc(RoundingMode),
complement(int)
ORACLES: classify()==FINITE for finite inputs; toDouble() not -Infinity; zero when op yields zero;
isInfinite() false
CASES: divide by tiny divisor, sqrt of tiny positive, subtraction yielding near-zero, rounding after
underflow, boundary exponents
RISKS: testIssue567 code unseen; DfpField rounding mode unknown; sign errors in complement may cause
-Infinity