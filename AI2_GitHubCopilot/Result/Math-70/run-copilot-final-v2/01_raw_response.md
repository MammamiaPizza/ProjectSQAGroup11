TARGETS: BisectionSolver.solve(f,min,max), solve(min,max), solve(f,min,max,initial), no-arg
constructor

ORACLES: result must be within [min,max]; f(result) ≈ 0 within default function-value accuracy;
converge within allowed iterations

CASES: f(root) sign change; root at min boundary; root at max boundary; min>max
(IllegalArgumentException)

CASES: min==max; function is null (NPE guard); large interval; small interval near machine epsilon

RISKS: UnivariateRealSolverImpl internals unknown; setFunction/getFunction behavior from parent
class assumed but unseen