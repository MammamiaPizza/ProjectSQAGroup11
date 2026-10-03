TARGETS: chiSquare(double[],long[]) statistic, including expected/observed total rescaling behavior.
TARGETS: chiSquareTest(double[],long[]) p-value derived from the chi-square statistic.
ORACLES: Trigger expected statistics: 9.023307936427388 and 114875.90421929007.
CASES: Trigger-scale expected/observed arrays that expose incorrect statistic calculation.
CASES: Equal expected and observed totals as normal control; unequal totals as key boundary/normal case.
RISKS: Public alpha overload and contingency-table/data-set methods are available but no failing behavior is specified.
RISKS: Exact input arrays and p-value expectations are unavailable; derive only from existing tests/specification context.