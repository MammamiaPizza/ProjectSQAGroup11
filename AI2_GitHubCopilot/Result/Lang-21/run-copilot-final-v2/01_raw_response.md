TARGETS: isSameLocalTime(Calendar,Calendar) — local time comparison ignoring time zone/DST
ORACLES: LANG-677: returns true iff HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND equal across any TZ
CASES: cal1=UTC 12:30:00.000, cal2=EST 12:30:00.000 → true (same local time, different instants)
CASES: cal1=UTC 12:30:00.000, cal2=UTC 12:30:01.000 → false (seconds differ)
CASES: DST spring‑forward gap (2:30 AM): both calendars may be adjusted; verify result (likely false
if fields differ)
CASES: cal1 and cal2 reference different Calendar types (GregorianCalendar vs BuddhistCalendar) but
same local time
RISKS: Null arguments → NPE? test must use non‑null; input validation not in scope
RISKS: Local time during DST fall‑back (1:30 AM ambiguous) may produce inconsistent results
depending on Calendar’s internal representation