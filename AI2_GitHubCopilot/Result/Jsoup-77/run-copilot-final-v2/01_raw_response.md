TARGETS: XmlTreeBuilder.popStackToClose(Token.EndTag) — tag matching in the element stack
TARGETS: XmlTreeBuilder.process(Token) — end-tag case normalization before popping
ORACLES: Compare Document.outerHtml() with expected normalized string after parsing
CASES: start lower/end upper, start upper/end lower, same‑case, nested mismatched pairs
CASES: one tag with attributes, multiple discordant siblings, self‑closing tags
RISKS: only tests XML case‑normalization; does not cover other XML‑specific edge cases (namespaces,
CDATA)