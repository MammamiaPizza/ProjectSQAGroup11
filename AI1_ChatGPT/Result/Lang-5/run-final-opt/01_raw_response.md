TARGETS: LocaleUtils.toLocale(String), especially parsing a leading-underscore locale string.  
ORACLES: Existing LocaleUtilsTest::testLang865 and LANG-865 trigger behavior.  
CASES: "_GB" must be covered; it currently throws IllegalArgumentException.  
RISKS: Test body and intended Locale result are unavailable; avoid assuming unsupported format semantics.