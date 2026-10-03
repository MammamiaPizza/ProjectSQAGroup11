TARGETS: ZoneInfoCompiler.parseDataFile and Zone.buildDateTimeZone/addToBuilder via testDateTimeZoneBuilder.
TARGETS: DateTimeOfYear.addCutover and Rule.addRecurring affect compiled transition rules.
ORACLES: Trigger testDateTimeZoneBuilder assertions are the only stated expected-result source.
ORACLES: DateTimeZoneBuilder-produced zones and ZoneInfoCompiler.test provide observable zone behavior.
CASES: Parse rule/zone data that creates normal recurring transitions and zone aliases.
CASES: Exercise cutover years, until dates, day-of-week forms, and time zone suffix characters.
CASES: Cover parseYear/month/day/time invalid or boundary tokens only if accepted input syntax is known.
RISKS: No failing assertion details, source data, or intended transition values are provided.
RISKS: Avoid assuming behavior from another version or undocumented TZ-data semantics.