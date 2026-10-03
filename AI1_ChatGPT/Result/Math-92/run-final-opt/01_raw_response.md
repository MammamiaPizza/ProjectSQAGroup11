TARGETS: MathUtils.binomialCoefficient(int n,int k), especially large exact long results and its arithmetic path.
ORACLES: Trigger supplies exact oracle: binomialCoefficient(48,22) == 27385657281648L.
CASES: Normal symmetry pairs (n,k)/(n,n-k); verify exact equality where independently known in test context.
CASES: Boundaries k=0, k=n, k=1, and large valid n/k near the trigger’s magnitude.
RISKS: Off-by-one integer result from rounding/division order or overflow-avoidance arithmetic.
RISKS: Context lacks implementation and broader documented validation/exception behavior; avoid assuming it.