TARGETS: Fraction integer-overflow detection in arithmetic/normalization paths.
ORACLES: Trigger expects an exception; use existing FractionTest::testIntegerOverflow behavior.
CASES: Operands near Integer.MAX_VALUE/MIN_VALUE that overflow intermediate or final int results.
CASES: Include successful near-limit operations that remain representable.
RISKS: Exact overflowing method/inputs are not provided beyond the trigger name and failed exception expectation.