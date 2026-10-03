TARGETS: translate(CharSequence,int,Writer) unescapes numeric HTML entities (&#\d+; or
&#x[0-9a-fA-F]+;) to Unicode.
ORACLES: Valid entity → write Unicode char; invalid/malformed/out-of-bounds → return 0, no output,
no exception.
CASES: Valid decimal (&#65;→A), hex (&#x41;→A), semicolon missing (e.g., &#65), out-of-bounds index,
empty input, index==len-1, index pointing beyond string, large code point 1114111, negative
codepoint, no '#' after ampersand.
RISKS: Specification only from Javadoc; bug cause (missing bounds check) not fully visible; other
invalid patterns (overlong, surrogate) untested.