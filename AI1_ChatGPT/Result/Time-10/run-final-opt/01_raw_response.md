TARGETS: BaseSingleFieldPeriod.between(ReadablePartial, ReadablePartial, ReadablePeriod).
ORACLES: Existing TestDays/TestMonths MonthDay daysBetween/monthsBetween trigger expectations.
CASES: MonthDay containing day 29 where an intervening/non-leap year has February 28.
CASES: Verify daysBetween and monthsBetween return normally for MonthDay partials, not IllegalFieldValueException.
CASES: Normal MonthDay partial pairs and boundary February day 28/29 combinations.
RISKS: Partial chronology resolution may construct an invalid February 29 while computing field differences.
RISKS: Context exposes only BaseSingleFieldPeriod; concrete factory APIs inferred solely from named triggers.