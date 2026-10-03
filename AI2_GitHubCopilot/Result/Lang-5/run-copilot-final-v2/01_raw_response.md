TARGETS: LocaleUtils.toLocale(String), especially handling _XX (country-only), missing language
ORACLES: Valid from java.util.Locale constructors; check getLanguage()/getCountry()/getVariant()
post-toLocale
CASES: "GB" (trigger), "GB", "en", "en_GB", "en_GB_Win", "en__Win", "", null, "en", "__", "_GB_Win",
"en_GB_Win_X"
RISKS: Only toLocale is modified; no access to post-fix code; spec from bug summary alone