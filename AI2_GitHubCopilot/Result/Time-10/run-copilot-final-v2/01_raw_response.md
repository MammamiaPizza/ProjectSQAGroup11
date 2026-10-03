TARGETS: BaseSingleFieldPeriod.between(ReadablePartial,ReadablePartial,ReadablePeriod)
ORACLES: manual day/month differences; no IllegalFieldValueException
CASES: MonthDay(2,29)→(3,1),(2,28)→(2,29); leap-day combined with non-leap year defaults
RISKS: only MonthDay-type ReadablePartials trigger; test all day-of-month values that exceed month
length in default year