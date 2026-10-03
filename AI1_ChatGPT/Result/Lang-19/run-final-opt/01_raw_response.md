TARGETS: NumericEntityUnescaper.translate(CharSequence input, int index, Writer out).
ORACLES: Trigger tests and LANG-710 establish unfinished/out-of-bounds input must not throw StringIndexOutOfBoundsException.
CASES: Unfinished numeric entity beginning at a valid index near input end.
CASES: Entity parsing where required characters extend beyond remaining input length.
CASES: Valid complete numeric entity to guard normal translation behavior.
RISKS: Input literals and intended output for trigger tests are not provided in this context.
RISKS: translate writes through Writer and returns consumed length; exact expected values are unspecified.