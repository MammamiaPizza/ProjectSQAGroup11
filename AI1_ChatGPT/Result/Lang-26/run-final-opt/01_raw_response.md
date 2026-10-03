TARGETS: FastDateFormat pattern parsing/formatting for week-of-year fields near year boundaries.
ORACLES: Trigger expects Swedish "fredag, week 53", not "fredag, week 01".
CASES: Format the LANG-645 date with explicit Swedish locale and deterministic timezone.
CASES: Cover dates in week 53 and adjacent first-week-of-year boundary.
RISKS: Week-year/week-number semantics depend on Calendar locale settings and timezone.
RISKS: Exact trigger date/pattern is not provided; derive only from existing test context.