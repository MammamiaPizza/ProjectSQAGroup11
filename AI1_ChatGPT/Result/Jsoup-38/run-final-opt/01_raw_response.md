TARGETS: HtmlTreeBuilderState.process(Token, HtmlTreeBuilder) state dispatch for image/start-tag handling.
TARGETS: HtmlTreeBuilderState.anythingElse(Token, HtmlTreeBuilder/TreeBuilder) fallback token processing.
ORACLES: Trigger expectation: parsing image yields serialized output "<img />".
CASES: Parse an image start tag and assert conversion to an img element/output.
CASES: Verify normal img start-tag handling remains serialized as "<img />".
CASES: Boundary/error token behavior is unspecified; limit assertions to trigger-observed image conversion.
RISKS: Only enum signatures and one trigger assertion are provided; parser entry-point details are unavailable.
