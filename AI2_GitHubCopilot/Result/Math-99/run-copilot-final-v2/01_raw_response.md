TARGETS: gcd(int,int) overflow detection for Integer.MIN_VALUE and other boundary args.
TARGETS: lcm(int,int) overflow detection in multiplication and internal gcd.
ORACLES: Failing tests expect ArithmeticException on overflow; document or prior behavior.
CASES: gcd: Integer.MIN_VALUE, negative, zero, both zero, both same, coprimes.
CASES: lcm: a*b overflows int, one arg MIN_VALUE, large values, zero inputs.
RISKS: No explicit spec; only tests clue exact overflow triggers; unclear msg.