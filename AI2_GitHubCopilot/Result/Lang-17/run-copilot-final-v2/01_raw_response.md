TARGETS: CharSequenceTranslator.translate(CharSequence) and translate(CharSequence,Writer)
ORACLES: Fixed version correctly handles supplementary characters; identity translator round-trips;
escape output for supplementary codepoints preserves character or escapes as per spec
CASES: "𠮷A" (supplementary + BMP), "𠮷", "", "A", "A𠮷B", U+FFFF boundary, multiple supplementary,
null input, invalid surrogate pairs
RISKS: Bug is in base-class iteration (char-by-char vs codepoint-aware); no concrete implementation
provided—need minimal anonymous subclass to reveal bug; concrete escape implementations may cause
different failures