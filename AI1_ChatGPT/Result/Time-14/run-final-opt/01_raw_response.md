TARGETS: BasicMonthOfYearDateTimeField.add(ReadablePartial,int,int[],int) month/day adjustment for MonthDay.
TARGETS: add(long,int/long), set(long,int), and leap-related behavior may affect month transitions.
ORACLES: Triggered TestMonthDay_Basics expectations and reported --02-29 versus --02-28 assertion.
ORACLES: Avoid IllegalFieldValueException for day 29 when operating from leap-day MonthDay values.
CASES: Add/subtract months from --02-29, including negative month deltas and transitions to/from February.
CASES: Add/subtract days from --02-29 where resulting MonthDay must remain valid.
CASES: End-of-month adjustment paths that should preserve --02-29 per listed trigger expectations.
RISKS: Partial-field add may validate day against a non-leap reference year, yielding range [1,28].
RISKS: Context lacks implementation and complete trigger assertions; derive expectations only from supplied failures.