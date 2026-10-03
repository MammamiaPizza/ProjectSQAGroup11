TARGETS: addYears(0),addDays(0),addWeeks(0),addMonths(0),add(DurationFieldType,int)(0)
ORACLES: offset unchanged after adding zero (expected +01:00, actual +02:00 from DST-overlap-winter
test failures)
CASES: DST overlap winter 2011-10-30T02:30+01:00[Europe/Berlin] add zero for each method
CASES: DST gap spring (e.g., 2011-03-27T02:30+02:00) add zero should preserve +02:00 offset
CASES: non-DST time (e.g., 2011-06-01T02:30+02:00) add zero should preserve offset
CASES: add different amounts (e.g., +1 year) to verify offset change logic remains correct
RISKS: Only these add-methods with zero tested; other MutableDateTime.add overloads untested for
DST-overlap no-op
RISKS: Tested only with Europe/Berlin; other DST timezones (e.g., US/Eastern) may differ
RISKS: No official spec for zero-add offset preservation; expected behavior derived from fixing test
assertions