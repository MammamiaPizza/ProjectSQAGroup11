TARGETS: Document.title(), Document.title(String), and title text-node handling in head/title elements.
ORACLES: Trigger specifies title after setting "Hello there now" must return exactly "Hello there now".
CASES: New Document/createShell: set ordinary title; assert title() round-trips and head() contains title content.
CASES: Set title containing spaces; verify spaces between words are preserved, not truncated at a newline/text node.
CASES: Existing/multiple title text nodes or embedded whitespace: title() should yield documented trimmed title.
CASES: No title element: title() returns empty string per API documentation.
RISKS: Context lacks implementation and parser/Element behavior; avoid assumptions about normalization or HTML serialization.