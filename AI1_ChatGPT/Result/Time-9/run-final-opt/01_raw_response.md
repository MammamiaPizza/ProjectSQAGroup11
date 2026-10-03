TARGETS: DateTimeZone.forOffsetHoursMinutes(int hoursOffset, int minutesOffset).
ORACLES: Returned zone offset/ID via public getOffset(long), getID(), or equality with UTC where applicable.
CASES: Normal positive and negative hour/minute combinations accepted by the factory.
CASES: Boundary minute values and zero-hour with signed minute offsets.
CASES: Invalid hour/minute ranges and inconsistent signs should throw IllegalArgumentException if enforced.
RISKS: Only trigger assertion is known; exact expected values and validation rules are not supplied.
RISKS: Avoid assumptions from other versions or unlisted APIs/formatting conventions.