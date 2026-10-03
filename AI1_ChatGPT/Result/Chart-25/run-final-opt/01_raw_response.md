TARGETS: StatisticalBarRenderer drawing with null mean or null deviation in vertical and horizontal plots.
ORACLES: Existing four trigger tests; assertion outcomes are the only stated expected-result source.
CASES: Null mean, vertical; null deviation, vertical; null mean, horizontal; null deviation, horizontal.
CASES: Verify drawing completes according to trigger-test assertions for each orientation/null field combination.
RISKS: Drawing methods are not listed in available signatures; inspect inherited/implementation paths before testing.
RISKS: No bug report or explicit rendering expectation is provided beyond failing trigger assertions.