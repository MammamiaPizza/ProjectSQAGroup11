TARGETS: FromStringDeserializer.Std Locale _deserialize via ObjectMapper/readValue(String, Locale.class).
ORACLES: Trigger specifies JSON locale "en_US" must deserialize to Locale en_US, not Locale("en-us").
CASES: Normal: underscore language/country input "en_US"; assert Locale equality and toString "en_US".
CASES: Boundary: language-only locale string if supported; empty-string handling via _deserializeFromEmptyString.
CASES: Error: malformed locale text behavior only if established by current-version tests/API.
RISKS: Locale parsing may normalize hyphenated input differently; do not assume unsupported-format semantics.
RISKS: Context lacks implementation/body and existing test expectations beyond testLocale.