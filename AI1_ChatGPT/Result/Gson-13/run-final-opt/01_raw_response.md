TARGETS: JsonReader.peekNumber(), peek(), nextString() handling of numeric literal "-0".
ORACLES: Trigger expects nextString() to preserve "-0", not normalize it to "0".
CASES: Reader over "-0": peek NUMBER then nextString returns "-0".
CASES: "-0" in arrays/objects where normal token sequencing applies.
CASES: Contrast "0", negative nonzero, and decimal/exponent numeric literals.
RISKS: Numeric parsing may lose lexical sign when converting/storing zero.
RISKS: Context lacks source body and broader numeric-string behavior; avoid unsupported expectations.