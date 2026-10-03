TARGETS: validateCharset(String) – normalization/fallback logic for unsupported charsets.
TARGETS: getCharsetFromContentType(String) – extraction of charset from Content-Type header.
TARGETS: parseInputStream(…) – charset resolution & fallback during parse.
ORACLES: validateCharset must return "UTF-8" for unknown/unsupported charset names.
ORACLES: Charset.forName(cs) availability indicates supported; else fallback to UTF-8.
ORACLES: getCharsetFromContentType must detect charset per HTML5/meta/header rules.
CASES: Unsupported charset (e.g., "ISO-2022-CN") → validateCharset returns "UTF-8".
CASES: Null/empty/blank charset input → default "UTF-8" behavior.
CASES: Common aliases (e.g., "UTF8", "iso-8859-1") → normalized canonical name.
RISKS: Charset mapping depends on JVM's available charsets; may vary across environments.
RISKS: Only one trigger test given; hidden charset-edge cases may exist.