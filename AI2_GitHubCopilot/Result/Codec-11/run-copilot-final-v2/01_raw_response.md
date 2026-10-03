TARGETS: encodeQuotedPrintable(BitSet,byte[]) for soft breaks and trailing specials;
decodeQuotedPrintable(byte[]) for CR/LF handling.
ORACLES: RFC 2045 §6.7 rules: soft line break "=\r\n" encode/ignore, trailing space/tab quoted, '='
as "=3D".
CASES: plain ASCII round-trip; long lines force soft breaks; CRLF sequences; trailing space/tab/'=';
decode lone "= " hex error.
RISKS: CR or LF after '=' must not be parsed as hex digits; decode must skip "=\r\n" soft breaks
without exception.