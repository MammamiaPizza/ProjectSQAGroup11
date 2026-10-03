TARGETS: HtmlTreeBuilderState.process handling start/end tags for known empty rawtext elements style
and noframes; exit from rawtext/RCDATA back to InHead.
TARGETS: Meta start tag immediately after empty style/noframes must be parsed as a structural token,
not emitted as text.
ORACLES: HtmlParserTest.handlesKnownEmptyStyle and handlesKnownEmptyNoFrames expected strings: <meta
name="foo"></head><body>One</body></html>.
ORACLES: Assert output contains no escaped <meta name=foo> and no duplicated </head><body>.
CASES: Normal: <head><style></style><meta name="foo"></head><body>One</body></html>; same shape with
noframes.
CASES: Boundary: known-empty style/noframes with meta attributes/self-closing and surrounding
whitespace; restore head handling after rawtext end tag.
CASES: Error: unclosed style/noframes then meta/head/body; nested rawtext tags; rawtext content
should not be tokenized as markup.
RISKS: Only HtmlTreeBuilderState is modified; no diff/source shown, so exact fix and tokenizer
interaction are inferred from assertion strings.
RISKS: Avoid inventing APIs or other Jsoup versions; scope tests to the two supplied parser cases
and same-state sibling rawtext elements.