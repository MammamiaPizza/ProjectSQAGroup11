TARGETS: Element.outerHtmlHead(Appendable,int,Document.OutputSettings) and html()/toString() serialization.
ORACLES: Trigger ElementTest.testKeepsPreTextAtDepth; expected output begins with `code[` per failure.
CASES: Nested `pre`/`code` text at depth; verify whitespace/newlines are preserved during serialization.
CASES: Compare serialization of preformatted content at root versus nested depth.
CASES: Include text containing leading newline/indentation inside preformatted elements.
RISKS: Pretty-print indentation may be inserted into preserved pre text.
RISKS: Available context truncates the full expected string and Element implementation.