TARGETS: NumericEntityUnescaper.translate(CharSequence,int,Writer), especially for supplementary
codepoints (>&#x;FFFF).
ORACLES: Expected output from Character.toChars(int codepoint); repetition of chars in Writer;
emulate existing BMP tests for higher values.
CASES: &#x41; -> "A" (BMP), &#x10400; -> "\uD801\uDC00" (supplementary), &#x10FFFF; (max codepoint),
decimal &#66560; same, &#x; without semicolon (if OPTION. semiColon), invalid >0x10FFFF, empty
entity.
RISKS: Need to know if class has OPTION enum for semicolon handling; only translate method is
testable; cannot verify other translators; error handling with invalid codepoints may be undefined.