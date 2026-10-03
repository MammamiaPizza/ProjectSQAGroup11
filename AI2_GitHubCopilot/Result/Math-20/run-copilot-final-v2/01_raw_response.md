TARGETS: CMAESOptimizer.doOptimize, FitnessFunction.repairAndDecode, repair, isFeasible, encode,
decode
ORACLES: Repaired points must stay within lower/upper bounds; out-of-bounds triggers test failure
from MATH-864.
CASES: Boundary values exactly at bounds, slightly out-of-bounds, feasible region shrinking;
high-dim simple objective (sphere).
RISKS: Only signature-level info; internal repair logic and penalty unknown; no corrected version
available.