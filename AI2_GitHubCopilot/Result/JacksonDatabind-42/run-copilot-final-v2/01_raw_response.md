TARGETS: FromStringDeserializer.Std._deserializeFromEmptyString() for kind STD_LOCALE (8)
TARGETS: FromStringDeserializer.Std._deserialize(String,DeserializationContext) for Locale
ORACLES: Locale.ROOT for empty string input (toString() empty, getLanguage() empty, getCountry()
empty)
ORACLES: Locale constructor parsing for "en_US" -> Locale("en","US"), "fr" -> Locale("fr")
CASES: Normal: "en","en_US","fr","ja_JP"; Boundary: "" (empty string), " "; Error: "xx_YY" raises
exception
RISKS: Other Std kinds (Charset,TimeZone,etc.) may also lack non-null empty-string handling
RISKS: No implementation source; must infer from trigger failure of TestJdkTypes.testLocale
RISKS: Must confirm _deserializeFromEmptyString() is invoked for empty-string JSON token in
deserialize()