TARGETS: Period.normalizedStandard() and normalizedStandard(PeriodType).
ORACLES: Existing TestPeriod_Basics normalizedStandard periodType_months1/months2/monthsWeeks assertions.
CASES: Normalize periods using a months-only PeriodType; verify no unsupported-field exception.
CASES: Normalize year/month values into a months-only result type.
CASES: Normalize mixed month/week values using a months-and-weeks PeriodType.
RISKS: Unsupported year assignment when target PeriodType excludes years.
RISKS: Context lacks exact trigger inputs and asserted normalized field values.