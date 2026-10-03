TARGETS: DataUtil.getCharsetFromContentType(String); DataUtil.parseByteData/load charset detection and fallback.
ORACLES: Triggered DataUtilTest and ParseTest expected no IllegalCharsetNameException; parsed charset handling.
CASES: Content-Type charset=iso-8859-1 should yield ISO-8859-1 without retaining "charset=".
CASES: Empty charset, quoted 'UTF-8', trailing comma, duplicate/multiple charset strings, and UTF-8" suffix.
CASES: Illegal charset name $HJKDF§$/( should return null rather than throw.
RISKS: Package-private methods may require tests in org.jsoup.helper; exact normalization source is limited to triggers.