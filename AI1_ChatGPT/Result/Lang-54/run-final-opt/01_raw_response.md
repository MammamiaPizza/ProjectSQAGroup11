TARGETS: LocaleUtils.toLocale(String), especially parsing "fr__POSIX" from LANG-328.
ORACLES: Trigger expects no IllegalArgumentException for "fr__POSIX"; Locale construction result is the source.
CASES: Normal language, language_country, and language_country_variant inputs accepted by toLocale.
CASES: Boundary empty country with variant ("fr__POSIX"); verify language, country, and variant fields.
CASES: Invalid locale formats should retain existing IllegalArgumentException behavior where established.
RISKS: Context provides no full format rules or expected results for other malformed/null inputs.