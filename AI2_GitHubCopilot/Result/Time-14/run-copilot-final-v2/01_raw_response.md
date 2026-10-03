TARGETS: add(ReadablePartial,int,int[],int) — month arithmetic on MonthDay partial that validates
day-of-month
TARGETS: add(long,int) — instant-based month addition used by plusMonths
ORACLES: MonthDay.plusMonths(-1) on 2020-02-29 should yield --01-29, not throw; day must be valid in
target month
ORACLES: End-of-month adjustment: if original day equals last day of month, result should be last
day of target month
CASES: Feb 29 ± N months, target month with ≥29 days; Feb 29 to non-leap Feb (N mult of 12) → Feb 28
CASES: minusDays/plusDays from Feb 29 that cross month boundary (e.g., minusDays(1) → Jan 28)
CASES: PlusMonths from regular dates (Feb 28, Jan 31) to verify end-of-month logic; zero- and
large-offset adds
RISKS: Bug likely in add(ReadablePartial) day-of-month clamping/validation after shifting month;
exact code unknown
RISKS: Expected results inferred from trigger failures; cannot inspect source or internal validation
rules