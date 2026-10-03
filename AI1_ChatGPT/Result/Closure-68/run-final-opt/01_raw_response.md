TARGETS: JsDocInfoParser parsing of JSDoc that previously emits "Unexpected end of file".
ORACLES: Existing trigger testIssue477 and ErrorReporterParser warning output.
CASES: Reproduce issue 477 input; assert no extra EOF warning.
CASES: Nearby valid JSDoc/type annotation endings and multiline/block-comment termination.
RISKS: Most parser methods are private; test through existing parser/test harness only.
RISKS: Context omits test fixture input and public construction/parse entry points.