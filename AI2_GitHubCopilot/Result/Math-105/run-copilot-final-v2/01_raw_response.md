TARGETS: getSumSquaredErrors() after addData(double,double)/addData(double[][]), plus clear()/getN()
for state.
ORACLES: Assert getSumSquaredErrors() >= 0; check getTotalSumSquares() == getRegressionSumSquares()
+ SSE (tolerance).
CASES: Random (x,y) series; near-perfect fit; near-identical x values; mixed large magnitudes;
single point/equal-x (skip).
RISKS: Only public signatures available; exact SSE value may vary, assert nonnegative only; n<2 or
zero x-variance gives NaN/undefined.