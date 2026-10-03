TARGETS: FromStringDeserializer.deserialize for Locale (STD_LOCALE). Must correctly parse locale
strings.
ORACLES: Expected via new Locale(lang, cntry) or Locale.forLanguageTag; trigger test expected en_US,
got en-us.
CASES: Normal: "en_US"→Locale("en","US"), "zh_CN", "en", "en-US", "sr_Latn_RS". Empty→error/null.
Underscore-vs-hyphen.
CASES: Boundary: very long tag, special chars, no country. Error: malformed tags like "x", invalid
variants.
CASES: Verify equals, toString, getLanguage, getCountry match expected. Ensure no default-locale
dependency.
RISKS: Limited to signpost info; cannot inspect implementation. Must fail on buggy version, pass
after fix. Assume Locale.ROOT for stable comparison.