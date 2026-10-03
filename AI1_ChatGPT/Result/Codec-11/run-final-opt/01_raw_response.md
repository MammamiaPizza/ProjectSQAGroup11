TARGETS: encodeQuotedPrintable(BitSet,byte[]), decodeQuotedPrintable(byte[]), and byte[]/String encode-decode delegates.
ORACLES: Trigger test expectations and quoted-printable syntax: hex escapes, preserved CRLF, soft break "=\r\n".
CASES: Decode literal CRLF and soft line breaks without hex-digit DecoderException; verify decoded bytes/text.
CASES: Encode long lines requiring soft breaks, including ultimate trailing soft break and trailing special '='.
CASES: Cover normal printable bytes, non-printable/special bytes encoded as =HH, and null/object type behavior if specified.
RISKS: Exact line-wrapping positions and CRLF handling are central; supplied context omits complete expected strings/line limits.