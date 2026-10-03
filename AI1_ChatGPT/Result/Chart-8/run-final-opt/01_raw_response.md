TARGETS: Week(Date, TimeZone, Locale) week extraction; constructor chaining used by Week(Date).
ORACLES: Existing WeekTests::testConstructor expects week 35, but Chart-8b returns 34.
CASES: Date at/within expected week 35 using the trigger's time-zone and locale context.
CASES: Boundary dates around week transitions; validate getWeek() and getYearValue().
RISKS: Week numbering depends on Calendar locale/time-zone first-day/minimal-days settings.
RISKS: Trigger input details are unavailable; expected result source is limited to reported assertion.