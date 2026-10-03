TARGETS: BrentSolver.solve(min,max) and solve(min,max,initial) should throw IllegalArgumentException
for non-bracketing endpoints.
ORACLES: Must throw IllegalArgumentException when f(min)*f(max) > 0 (root not bracketed); must not
throw when <=0 (including root at endpoint). Based on MATH-343 fix expectation.
CASES: f(min)*f(max)>0 (non-bracketing), f(min)*f(max)<0 (valid bracketing), f(min)=0 or f(max)=0
(root at boundary), min==max, min>max, large domain, small domain.
RISKS: Only buggy-version context available; cannot consult fixed version. Deprecated
constructor(UnivariateRealFunction) may be needed if setFunction unreachable.