TARGETS: Week(Date,TimeZone,Locale), peg(Calendar), getWeek()
ORACLES: java.util.Calendar.get(Calendar.WEEK_OF_YEAR) for given tz/locale; testConstructor asserts
known dates
CASES: mid-year dates; week-1 boundary; week-53 boundary; leap-year dates; non-default time zone;
null TimeZone; explicit Locale
RISKS: locale-dependent first-week definition (minDaysInFirstWeek); peg may ignore
constructor-supplied tz/locale; default locale may differ from test expectations