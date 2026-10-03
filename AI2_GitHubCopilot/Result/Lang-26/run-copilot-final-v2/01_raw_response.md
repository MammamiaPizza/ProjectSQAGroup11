TARGETS: FastDateFormat.format(Calendar) with week-of-year pattern "w"
TARGETS: getInstance("w-MMMM-yyyy",...) with Swedish locale to reproduce LANG-645
TARGETS: selectNumberRule for Calendar.WEEK_OF_YEAR and NumberRule implementations
ORACLES: Calendar.get(Calendar.WEEK_OF_YEAR) with Swedish locale's
firstDayOfWeek/minimalDaysInFirstWeek
ORACLES: LANG-645 expected week 53 for sv_SE date; SimpleDateFormat output as reference
CASES: 2005-12-31 (year transition) in sv_SE to verify week 53 vs 1
CASES: Other locales with ISO 8601 week (Monday first, 4+ days) e.g., de_DE, fr_FR
CASES: Padded two-digit week "ww" for week 7 to ensure zero-padding
RISKS: Internal init() may not adjust Calendar minimalDaysInFirstWeek; root cause unknown
RISKS: Only testLan645 triggers failure; other week-of-year cases not covered