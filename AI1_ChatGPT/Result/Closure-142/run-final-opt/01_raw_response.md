TARGETS: CoalesceVariableNames.process/visit/LiveRangeChecker variable coalescing around parameters (testParameter4).
TARGETS: JsDocInfoParser block-comment extraction/parsing for @license text with annotations.
ORACLES: Existing trigger assertions: testParameter4 must not fail; testParseLicenseWithAnnotation expects " Foo".
CASES: Parameter declarations/usages whose live ranges must remain distinct during coalescing.
CASES: @license comment containing an annotation; preserve extracted license text/leading space as expected.
RISKS: Internal/private parser helpers limit direct unit access; test through existing parser-facing test utilities.
RISKS: No source/body context supplied; avoid assumptions beyond trigger names and stated expected text.