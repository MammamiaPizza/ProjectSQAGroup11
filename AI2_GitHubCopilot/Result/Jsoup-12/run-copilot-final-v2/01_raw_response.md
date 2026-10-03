TARGETS: Selector constructor/select() parsing logic, especially attribute selector value
extraction.
ORACLES: Expected parsing success; behavior defined by Jsoup CSS selector docs and test assertions.
CASES: Selectors with | inside [attr~=v], [attr*=v], [attr^=v], [attr$=v] (regex combinators).
CASES: Selectors combining attribute regex with other filters or combinators (descendant/child).
CASES: Boundary: | at start/end of value, escaped \|, consecutive |, empty regex.
CASES: Error: malformed queries like missing ], [attr] without value, unclosed quotes.
RISKS: Internal tokenization may treat | as CSS namespace combinator, breaking attribute value
parsing.
RISKS: Fix might affect other selectors using | outside attributes (e.g., a|b combinator).
RISKS: Only partial API visible; actual parsing internals not in provided signatures.