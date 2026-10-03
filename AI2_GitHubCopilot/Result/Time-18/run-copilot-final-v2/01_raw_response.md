TARGETS: assemble(Fields) leap-year dayOfMonth max; CutoverField.getMaximumValue for dayOfMonth;
isLeap; convertByYear using cutover
ORACLES: Gregorian calendar rules (Feb29 valid only in leap years); testLeapYearRulesConstruction
expects no IllegalFieldValueException
CASES: Normal: construct with cutover after leap year (e.g. 2001), Feb29 usable. Boundary:
cutover=Feb29 leap year. Error: Feb29 in non-leap Gregorian year after cutover
RISKS: No reference to fixed version; expected behavior inferred from bug report; interplay of
cutover and leap year may hide additional issues