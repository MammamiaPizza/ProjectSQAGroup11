TARGETS: formatPeriodISO, formatDurationISO, reduceAndCorrect
ORACLES: Expected ISO duration strings from manual calculation; failing case expects "09" not "-2"
CASES: start>end (negative period producing incorrect sign), zero duration, crossing month/year
boundaries, leap year, DST transitions, large durations, equal start/end
RISKS: Only one test failure known; bug likely in reduceAndCorrect corrections; formatDuration and
formatPeriod may also exhibit sign errors