TARGETS: process(Token) token handling; insertNode for empty-block insertion; formattingElements
management.
ORACLES: HtmlParserTest::handlesKnownEmptyBlocks expected output (comparison of parsed HTML
strings).
CASES: Known empty blocks (img, hr, br, input, etc.) alone, nested, with attributes, with text
siblings, self-closing tags.
RISKS: Only bug-trigger test visible; exact fix unknown; need to infer empty-block handling from
actual/expected diff.