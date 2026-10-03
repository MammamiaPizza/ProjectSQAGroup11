TARGETS: Entities.escape(String), escape(Writer,String) – char-to-entity conversion for code points
above 0xFFFF (surrogate pairs).
TARGETS: Entities.entityName(int) – numeric fallback when no named entity exists for high code
points.
ORACLES: For U+1D476 (decimal 119650), escape must yield &#119650;, not split surrogates
&#55348;&&#57186; (expected per Unicode).
ORACLES: Expected: any supplementary code point → single &#decimal;; BMP chars retain named/numeric
escapes as before.
CASES: Normal: U+1D476, U+10000 (🄀), U+10FFFF. Boundary: U+FFFF, U+10000, max code point. Edge:
null/empty input, mixed BMP+supplementary.
CASES: Error: very long strings, characters without named entity – confirm no split surrogates.
CASES: Test round‑trip: unescape(escape(…)) for supplementary chars (if unescape is fixed or
unaffected).
RISKS: Fix not visible; escape may still produce wrong output for other high codepoints; unescape
may mishandle existing numeric &#119650;.
RISKS: EntityMap lookup methods (LookupEntityMap, ArrayEntityMap) may treat keys as int – need to
handle values > 0xFFFF correctly.
RISKS: Only one trigger test case provided; other edge conditions like max code point 0x10FFFF
unknown.