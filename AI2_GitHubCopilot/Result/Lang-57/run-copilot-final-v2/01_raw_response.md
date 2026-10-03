TARGETS: toLocale, availableLocaleSet, availableLocaleList, isAvailableLocale, localeLookupList,
languagesByCountry, countriesByLanguage
ORACLES: LocaleUtils javadoc; Locale constructors; Locale.getAvailableLocales(); static map content
reverse-lookup via Locale.getISOCountries/Languages
CASES: toLocale null/empty, "en", "en_US", "en_US_WIN"; isAvailableLocale existing/non-existing;
localeLookupList with/without default; languagesByCountry null/valid/invalid country
RISKS: NPE origin unclear; static maps may be unpopulated; need to reproduce all trigger NPEs before
fixing