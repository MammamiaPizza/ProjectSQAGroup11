TARGETS: OptionBuilder.withDescription, reset/create lifecycle
TARGETS: OptionBuilderTest::testBuilderIsResettedAlways
ORACLES: assertion error "we inherited a description" on reuse
ORACLES: JUnit 3.8.1; compare created Option against expected fresh state
CASES: call withDescription then create, verify no stale description leaks
CASES: chain multiple builder calls, check isolated option fields
RISKS: reset may miss descriptions; test may only check single field