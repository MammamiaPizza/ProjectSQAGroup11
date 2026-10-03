TARGETS: ObjectReader._initForMultiRead, _bindAndReadValues, readValues(InputStream),
readValues(byte[])
ORACLES: readValues must not throw CharConversionException for valid root-bean sequences
ORACLES: MappingIterator yields correct number and content of deserialized root beans
CASES: Empty input; single root; multiple root values separated by whitespace; root name wrapping
on/off
CASES: Valid surrogate pair (0xD800 0xDC00); lone high/low surrogate; code point exactly U+10FFFF;
U+10FFFE
CASES: Multi-byte BOM prefix; large buffer testing boundary reads; malformed byte sequence producing
0x2261223a
RISKS: UTF-32 decoding may live in Jackson Core, not ObjectReader – only limited fix possible in
databind
RISKS: No access to original test fixture; must reconstruct trigger from error message char #1, byte
#7
RISKS: Behavior may vary with parser features (JsonParser.Feature) or factory settings, not fully
covered here