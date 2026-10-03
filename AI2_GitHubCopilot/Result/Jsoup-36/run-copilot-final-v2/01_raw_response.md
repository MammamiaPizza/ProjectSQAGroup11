TARGETS: DataUtil.getCharsetFromContentType(String) – must sanitize extracted charset: strip
quotes/comma/whitespace, return null for illegal/empty.
TARGETS: DataUtil.parseByteData(...) – handle null/illegal charset from getCharsetFromContentType
without throwing IllegalCharsetNameException; fallback to default.
ORACLES: For illegal/empty names (e.g. "$HJKDF§$/( ", " ") expect getCharsetFromContentType returns
null per shouldReturnNullForIllegalCharsetNames.
ORACLES: For malformed but recognizable names (quoted, trailing comna, extra quotes) expect cleaned
valid names per testQuotedCharset, testBrokenHtml5CharsetWithASingleDoubleQuote.
CASES: Normal: "UTF-8", "ISO-8859-1". Boundary: ""UTF-8"", "'UTF-8'", "ISO-8859-1,", "UTF-8"".
Error: empty "", illegal chars, null contentType.
RISKS: Fix must not break existing valid charset parsing; no NPE when null returned; ensure fallback
charset (UTF-8) not used wrongly where null expected.