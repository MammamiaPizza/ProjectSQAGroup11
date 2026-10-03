TARGETS: Fraction.compareTo(Fraction), especially unequal fractions with products exceeding int range.  
ORACLES: Trigger expects compareTo result -1 where buggy code returns 0; FractionTest is available context.  
CASES: Compare 1/2147483647 with 1/2147483646; expect negative ordering without cross-product overflow.  
CASES: Reverse operands; expect positive ordering; equal reduced/equivalent fractions should compare as 0.  
CASES: Negative vs positive and zero ordering; verify sign-consistent compareTo results.  
RISKS: Exact comparison contract beyond trigger is not supplied; avoid assuming constructor/error behavior.