TARGETS: Complex.divide(Complex), Complex.atan() — both fail in test suite
ORACLES: Complex arithmetic: divide by zero => poles give signed infinities; atan via log formula
yields infinite result at branch points
CASES: divide: (1+2i)/ZERO, ZERO/ZERO, INF/ZERO, (1+NaN)/ZERO; atan: I, -I, ZERO, INF, NaN, large
real values near branch cut
RISKS: Exact atan failure details not given; similar edge-case bugs likely in asin,acos,log,sqrt;
incomplete trigger message limits oracle precision