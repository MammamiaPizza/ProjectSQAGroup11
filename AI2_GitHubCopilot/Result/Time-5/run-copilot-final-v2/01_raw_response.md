TARGETS: Period.normalizedStandard(PeriodType) with restrictive types (months, monthsWeeks, weeks)
ORACLES: No UnsupportedOperationException; returned Period.getPeriodType() matches requested type
ORACLES: toStandardDuration().getMillis() approximately equals
original.toStandardDuration().getMillis()
CASES: P1Y1M normalized to months type -> years convert to months, total months=13, no exception
CASES: P1Y1M normalized to months type -> getMonths()==13, getYears()==0 (supported=months only)
CASES: P1Y normal to weeks type -> years convert to days then weeks; verify type=weeks, no exception
CASES: P1M3W normal to monthsWeeks type -> months stays, weeks stays; no exception, type matches
CASES: Zero period (ZERO) normalized to any type -> returns ZERO? or new period with all zeros; type
matches
CASES: Negative period P-1Y-1M normalized to months type -> months negative, no exception
RISKS: Exact duration equality may not hold due to floating/rounding in year/month conversions; use
tolerance