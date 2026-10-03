TARGETS: LocalDate.fromDateFields(Date)/fromCalendarFields(Calendar) year mapping for BCE dates.
TARGETS: LocalDateTime.fromDateFields(Date)/fromCalendarFields(Calendar) must preserve time
04:05:06.007.
TARGETS: LocalDate/LocalDateTime.toString() should emit corrected proleptic years (0000, -0002).
ORACLES: Trigger assertions expect 0000-02-03T04:05:06.007 and -0002-02-03T04:05:06.007; bug gave
0001/0003.
ORACLES: LocalDate expects 0000-02-03 and -0002-02-03; compare via toString() in JUnit asserts.
CASES: fromDateFields/fromCalendarFields for pre-year-zero dates with proleptic years 0 and -2.
CASES: Normal positive CE dates stay unchanged in both LocalDate and LocalDateTime factories.
CASES: Boundary era switch year 0 vs year 1 and negative-year off-by-one checks.
RISKS: Supplied API list truncated; rely only on listed static factories, no new methods.
RISKS: Exact input Date/Calendar construction not shown; infer cases only from trigger
names/expected strings.