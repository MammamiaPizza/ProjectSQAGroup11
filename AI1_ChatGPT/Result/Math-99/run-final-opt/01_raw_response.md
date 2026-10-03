TARGETS: MathUtils.gcd(int,int) and MathUtils.lcm(int,int) integer overflow handling.
ORACLES: Trigger tests specify ArithmeticException for relevant overflow inputs.
CASES: gcd with Integer.MIN_VALUE paired with 0, itself, and small/odd integers.
CASES: lcm with Integer.MIN_VALUE and 1, -1, 0, and factors causing int overflow.
CASES: Normal signed, zero, equal, coprime, and negative-input gcd/lcm results.
RISKS: Math.abs(Integer.MIN_VALUE) and negation can overflow silently.
RISKS: Exact expected normal results and all exception input pairs require existing-test inspection.