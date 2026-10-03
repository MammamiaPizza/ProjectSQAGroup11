TARGETS: DataUtil.getCharsetFromContentType(String) extracts and normalizes charset tokens.
ORACLES: Trigger expects "utf-8" for charset=UTF-8 and charset="UTF-8".
CASES: Unquoted and quoted charset parameters with uppercase charset value.
CASES: Verify case-insensitive charset parameter matching per charsetPattern.
RISKS: getCharsetFromContentType is package-private; tests need org.jsoup.helper package access.
RISKS: No behavior source given for absent, malformed, empty, or multiple charset parameters.