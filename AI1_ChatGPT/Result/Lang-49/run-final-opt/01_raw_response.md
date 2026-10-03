TARGETS: Fraction.reduce(), especially reduction of fractions with a zero numerator.
ORACLES: Trigger expects reduced numerator 1, not 100, for the exercised reduction case.
CASES: Reduce common-factor fractions; assert numerator/denominator reflect lowest terms.
CASES: Reduce zero numerator fractions; verify canonical behavior if established by existing tests.
CASES: Reduce already reduced fractions; verify returned value remains equivalent.
RISKS: Private GCD behavior is only indirectly testable through public reduce/getReducedFraction.
RISKS: Context omits trigger input and full existing-test expectations; derive only from available tests.