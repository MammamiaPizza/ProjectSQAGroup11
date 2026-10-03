TARGETS: GJChronology construction/date-field behavior for leap-year rules around Gregorian cutover.
ORACLES: Trigger expects leap-day construction not to reject day 29 with maximum 28.
CASES: Construct a leap-year Feb 29 using GJChronology; verify day-of-month accepts 29.
CASES: Compare normal February dates and non-leap February 29 rejection behavior.
CASES: Exercise dates near DEFAULT_CUTOVER where Julian/Gregorian field conversion applies.
RISKS: Available context lacks the exact triggering test inputs, cutover, zone, and expected instant.