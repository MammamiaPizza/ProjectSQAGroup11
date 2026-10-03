TARGETS: OpenMapRealVector.ebeDivide, ebeMultiply for IEEE 754 edge cases (0/0, 0Inf)
ORACLES: IEEE 754: 0.0/0.0→NaN, x/0.0→±Inf, 0.0Inf→NaN; existing SparseRealVectorTest assertions
CASES: ebeDivide: 0.0/0.0, 1.0/0.0, 0.0/1.0; ebeMultiply: 0.0Inf, -InfInf, NaN*any; zero-default
entries skipped
RISKS: OpenMapRealVector skips default-zero entries; must force storage to cover NaN/Inf; tolerance
sets isDefaultValue threshold