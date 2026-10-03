TARGETS: forOffsetHoursMinutes(int hoursOffset, int minutesOffset)
ORACLES: valid total offset [-23:59:59.999,+23:59:59.999]; minutes -59..59 inclusive; throw
IllegalArgumentException otherwise
CASES: normal: (1,0),(1,30); mixed sign? (1,-15) amt 45m; bug fix: (-1,-15) works; boundary:
(−23,−59),(23,59),(0,±59),(±24,0)→invalid; minutes ±60→invalid
RISKS: unclear if minutes must match hour sign or total millis only; precise exception messages
untested; only forFixedOffsetZone visible