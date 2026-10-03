TARGETS: MathUtils.factorialDouble(int); trigger is factorial precision at n=17.
ORACLES: Existing MathUtilsTest::testFactorial expected 17! = 3.55687428096E14.
CASES: factorialDouble(17) exact double comparison; nearby valid n values if existing expectations support them.
CASES: factorial(int) vs factorialDouble(int) where factorial long result is representable.
RISKS: Do not infer behavior for negative inputs, overflow limits, or other MathUtils methods from this context.
RISKS: No source body or full existing test expectations are provided; avoid using another version.