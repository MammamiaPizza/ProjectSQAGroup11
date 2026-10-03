TARGETS: FastDateParser.parse(String) and parse(String, ParsePosition) regex consumption for pattern "M E".
ORACLES: LANG-831 triggers require "3 Tue" to fail, not yield Mon Mar 02 21:00:00 PST 1970.
CASES: Parse "3 Tue" with pattern "M E" under the trigger locale/time-zone setup; expect ParseException/null.
CASES: Check ParsePosition failure behavior for the same malformed/partially matched input.
CASES: Preserve valid complete numeric/month and weekday parsing where applicable.
RISKS: Constructor is protected; tests may need existing package-level test access or FastDateFormat.
RISKS: Exact expected dates depend on locale, timezone, calendar defaults, and leniency context not supplied.