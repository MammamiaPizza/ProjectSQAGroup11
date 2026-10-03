TARGETS: DataUtil.getCharsetFromContentType(String) normalized charset extraction.
TARGETS: DataUtil.parseByteData(ByteBuffer,String,String,Parser) applies charset to Document.
TARGETS: DataUtil.load(InputStream,String,String,Parser) with null/empty charsetName.
ORACLES: DataUtilTest.testCharset expects "utf-8" from content, not "UTF-8".
ORACLES: DataUtilTest.testQuotedCharset expects "utf-8" from quoted charset attr.
ORACLES: Bug 215 failing assertions give expected lowercase canonical charset names.
CASES: contentType "text/html; charset=UTF-8" (normal).
CASES: contentType "text/html; charset="UTF-8"" (quoted).
CASES: missing, malformed, or extra whitespace charset to verify fallback.
RISKS: only listed signatures available; getCharsetFromContentType body/tests not shown.