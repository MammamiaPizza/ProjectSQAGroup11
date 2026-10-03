TARGETS: solve(UnivariateRealFunction f, double x0, double x1)
ORACLES: Math root theorem; opposite-sign endpoints must converge to root; IAE thrown if
f(x0)*f(x1)>=0; boundary root returns boundary.
CASES: root in middle: f(x)=x, [-1,1]; root at upper bound: f(x)=x-2, [1,2]; large upper bound with
f(b)=0; f(a)=0, f(b)>0; same-sign endpoints -> IAE; near-zero slope; sin(x) [π, 3π/2].
RISKS: Unknown maxIter & tolerance; bug may appear only with specific function+interval; no
reference impl; test only basic convergence & mathematical properties.