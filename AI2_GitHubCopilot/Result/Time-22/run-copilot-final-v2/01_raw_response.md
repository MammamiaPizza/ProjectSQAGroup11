TARGETS: BasePeriod(long duration, PeriodType type, Chronology chrono) and related constructors;
checkAndUpdate; setPeriodInternal.
ORACLES: For a Duration converted to Period in a fixed zone, all period fields from zone offset must
be zero (e.g., hours=0 not 64).
CASES: zero duration, millis-only, hours-only, days-only; boundary at DST transition; large millis;
unsupported field type; null chronology/zone.
RISKS: Limited to abstract class signature; cannot see private field normalization; bug triggered
only with fixed zone, might miss other zone/chronology combos.