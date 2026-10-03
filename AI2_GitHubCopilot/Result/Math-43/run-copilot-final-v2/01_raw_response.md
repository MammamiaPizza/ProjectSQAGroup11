TARGETS: addValue must propagate values to overridden Mean/GeoMean/Variance impl
TARGETS: setMeanImpl, setGeoMeanImpl, setVarianceImpl impact getter correctness
ORACLES: expected from direct formula (sum/N, product^(1/N), etc.) or known inputs
CASES: set a plain Mean() impl, add {1,4}/getMean, expect 2.5 not NaN
CASES: set a plain GeometricMean() impl, add {2,4,8}/getGeometricMean, expect ~4 (∛64)
CASES: set a plain Variance() impl, add {1,2,3,4}/getVariance, expect 1.666...
CASES: addValue with no values after override, getMean/getGeoMean/getVariance → NaN
CASES: copy() after overriding should transfer active impl and its state
RISKS: addValue may ignore overridden sumLog/secondMoment; copy/clear may reset state inconsistent
with overrides