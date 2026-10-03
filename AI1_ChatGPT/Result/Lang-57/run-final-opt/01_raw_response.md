TARGETS: LocaleUtils static initialization; toLocale, lookup lists, available locale list/set, availability, language/country maps.  
ORACLES: Existing trigger test names and JDK Locale behavior/available locales; no alternate project version supplied.  
CASES: Construct LocaleUtils; call every public static API without prior setup; assert no initialization NPE.  
CASES: toLocale 1-, 2-, and 3-part locale strings; valid language/country/variant parsing.  
CASES: localeLookupList with locale alone and explicit default locale; fallback ordering/content.  
CASES: availableLocaleList/set consistency; isAvailableLocale for known available and unavailable Locale.  
CASES: languagesByCountry and countriesByLanguage for populated, empty, and null code inputs if trigger coverage permits.  
RISKS: Exact parsing, lookup ordering, null-input, and empty-result contracts are not provided in this context.