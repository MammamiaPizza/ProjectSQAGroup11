TARGETS: Entities.escape(String, OutputSettings) and escape(String, CharsetEncoder, EscapeMode)
TARGETS: Entities.unescape(String), especially replacement handling for quotes and entity matches
ORACLES: Existing trigger tests and EntitiesTest expectations are the available behavior source
CASES: Escape normal text, quotes, ampersands, angle brackets, and characters needing named/numeric entities
CASES: Boundary entity-like inputs: empty/malformed references, missing semicolon, numeric hex/decimal forms
CASES: Error-prone replacements containing '$' or '\' and quote characters; assert no IllegalArgumentException
CASES: Parse Yahoo-style article content if fixture/input is available through existing trigger test
RISKS: APIs are package-private; tests may need placement in org.jsoup.nodes
RISKS: No entity-table details or exact expected encodings are provided; avoid inventing mappings