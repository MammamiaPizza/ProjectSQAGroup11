TARGETS: getDateInstance(style), getTimeInstance(style), getDateTimeInstance(style), getInstance() —
all no-locale-arg static factories.
TARGETS: Caching maps (cDateInstanceCache, etc.) that may stale when default locale changes.
ORACLES: Factory-returned instance's locale (getLocale()) must equal Locale.getDefault() at call
time.
ORACLES: Post locale switch, new factory call must not return cached instance with old locale (test
saw en_US, expected de_DE).
CASES: Set default to GERMANY, get date instance (no locale), switch to US, get same style, verify
locale is US.
CASES: Repeat for time, date-time, and pattern-based getInstance() factories.
CASES: Chain multiple default-locale changes, checking each factory bears current default.
RISKS: Truncated API may lack getLocale(); assume its existence for test.
RISKS: Unclear if getInstance(String) also cached; need to verify test failures only mention
Date/DateTime.