TARGETS: DateUtils.truncate(Calendar,int) — DST timezone shift (MDT→MST); truncate(Date,int) via
same modify() path; also round(Calendar,int).
ORACLES: After truncate, Calendar.getTime().toString() timezone must match input abbreviation;
millis must equal expected pre-truncated value and UTC_TIME_ZONE comparison may help.
CASES: Truncate to SECOND/MINUTE/HOUR for a Date in the DST fall-back ambiguous hour (e.g.
2004-10-31 01:02:03 MDT/MST); test spring-forward gap; test DAY/MONTH/YEAR truncation for DST
stability; boundary: exactly at transition times.
RISKS: DST transition rules depend on JVM timezone data; only one known trigger failure; expected
timezone behavior derived solely from existing test expectation; modify() is private, so testing
limited to truncate/round.