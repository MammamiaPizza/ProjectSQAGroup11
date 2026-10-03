TARGETS: Document.text() (getter), normalise(), Document.text(String) setter; possibly outerHtml().
ORACLES: Expected text order matches source HTML child element order; bug report expects "foo bar
baz" for body snippet.
ORACLES: Specification: node order in DOM should be preserved; text concatenation should be document
order.
CASES: Normal: <div>foo</div><div>bar</div><div>baz</div> → text()="foo bar baz".
CASES: Boundary: single element, empty document, only whitespace text nodes.
CASES: Nested: <p>hello <b>world</b></p> → text order "hello world".
CASES: After setText("new text") then getText() should return "new text"; after parse, normalise,
getText.
CASES: body().text() and head().text() should reflect their content order.
RISKS: Unknown if text() adds spaces; need to infer from test expectation (has spaces).
RISKS: Implementation could reorder due to normalise bug; limited API visibility beyond listed
signatures.