TARGETS: MathUtils.factorialDouble(int) – rounding error for n≤20 that yields exact long factorial.
ORACLES: Exact long factorial from MathUtils.factorial(n) or known factorial values for n≤20.
CASES: Normal:n=0,1,2,17,20; Boundary:n=21 (overflow); Error:negative n (expect
IllegalArgumentException).
RISKS: For n>20 double may still have 1‑ULP errors after fix; no spec for large‑n precision.