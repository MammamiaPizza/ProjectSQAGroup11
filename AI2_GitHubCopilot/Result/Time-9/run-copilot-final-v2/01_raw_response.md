TARGETS: DateTimeZone::forOffsetHoursMinutes(int,int) – correct offset, ID, and zero-minutes
handling
ORACLES: totalMillis = hours3600000 + minutes60000; result.getOffset(0) == totalMillis
ORACLES: When minutes==0, result.equals(DateTimeZone.forOffsetHours(hours))
CASES: Normal (5,30) => offset 19800000; Zero minutes (5,0) same as forOffsetHours(5)
CASES: Negative (-3,-45); boundary (0,0) same as UTC?; (23,59), (-23,-59)
CASES: Error: hours>23 or <-23→IAE; minutes>=60 or <=-60→IAE
RISKS: Expected ID format unknown; forOffsetHours() may also be flawed; cannot confirm UTC ID for
(0,0)