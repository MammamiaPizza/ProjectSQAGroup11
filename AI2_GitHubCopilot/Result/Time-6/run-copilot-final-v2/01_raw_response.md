TARGETS: CutoverField.add() for year/weekyear fields when result crosses cutover into year 0 or
negative.
TARGETS: static convertByYear() and convertByWeekyear() mappings around year 0 boundary.
ORACLES: Expected no year 0 (IllegalFieldValueException); crossing positive→negative should yield -2
not -1.
ORACLES: Test expected values: <-0002-06-30> for plusYears/plusWeekyears positive→negative; no
exception for valid dates.
CASES: plusYears/plusWeekyears from positive to zero, positive to negative, crossing cutover
instant.
CASES: Boundary: add zero; add large deltas; dates exactly on cutover day; leap days (Feb 29) near
cutover.
CASES: cutoverPreZero: plusYears on dates before year 1 crossing the cutover (negative→positive?).
RISKS: convertByYear may not skip year 0, producing an invalid intermediate year in target
chronology.
RISKS: CutoverField.add() may call set(year=0) after conversion, triggering
IllegalFieldValueException.
RISKS: Weekyear conversion logic may differ from year conversion, causing off-by-one in negative
era.