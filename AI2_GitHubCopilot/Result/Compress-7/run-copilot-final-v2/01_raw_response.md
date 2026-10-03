TARGETS: formatNameBytes(String, byte[], off, len) and parseName(byte[], off, len) round-trip,
especially non-ASCII/8-bit names.
TARGETS: trailing NUL padding/truncation behavior in formatNameBytes for fixed-length tar name
fields.
ORACLES: trigger TarUtilsTest#testRoundTripNames expected "<...±±±...>MOE.model" vs actual
"<...ﾱﾱﾱ...>MOE.model".
ORACLES: assertEquals(inputName, parseName(formatNameBytes(inputName))) for the characters exercised
by the trigger.
CASES: ASCII-only names; names with U+00B1 (byte 0xB1), other Latin-1 high bytes, and short
multibyte sequences.
CASES: boundary name length at tar max 100, one less/longer, empty name, and names padded by NULs.
CASES: edge inputs: null name, null/empty buffer, negative/large offset, offset+length beyond buffer
for name methods.
RISKS: tar defines no charset; expected values come only from the trigger, so avoid inventing
encodings/APIs.
RISKS: JUnit 3.8.2 contract; avoid relying on newer String/charset or deprecated decode behavior for
assertions.