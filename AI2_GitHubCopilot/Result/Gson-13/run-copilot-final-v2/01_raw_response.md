TARGETS: JsonReader.nextDouble() and number-parsing in peekNumber
ORACLES: Double.doubleToLongBits(-0.0)==0x8000000000000000L; Double.compare
CASES: "-0", "-0.0", "-0e0", "-0E0" → -0.0; "0","0.0","0e0" → 0.0
CASES: "-0.0e-0" → -0.0; boundary "-00"; strict vs lenient
RISKS: nextLong/nextInt must return 0, not -0; positive zero unchanged
RISKS: Ensure -1.2, 1e3 parse correctly; no false negative zero