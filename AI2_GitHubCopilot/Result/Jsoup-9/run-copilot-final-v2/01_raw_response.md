TARGETS: Entities.unescape(String) – decode named and numeric HTML character references.
TARGETS: unescapePattern – regex for &name; &#dec; &#xhex; with optional semicolon.
ORACLES: HTML5 named references (e.g., &frac34; → ¾, < → <, & → &).
ORACLES: Numeric entities map to Unicode code points; verify via Integer.parseInt/Character.toChars.
CASES: Named with/without semicolon (&frac34 vs &frac34;), unknown names (&bad;), empty/malformed.
CASES: Decimal/hex numeric entities, boundary values (valid max code point, zero, negative).
CASES: Entities adjacent to text, at start/end, multiple entities, double-encoded (<).
RISKS: Internal named entity map is not visible; assume full HTML5 set, but gaps possible.
RISKS: Only unescape method is in scope; escape-mode behavior may differ; no full spec doc.