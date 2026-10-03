TARGETS: DateTimeZone.forOffsetHoursMinutes(int,int), especially negative hour with negative minutes.
ORACLES: Trigger expects no IllegalArgumentException for (-?, -15); resulting zone offset/ID via public accessors.
CASES: Normal: (0,0), positive hours/minutes, negative hours with positive minutes.
CASES: Boundary: minute ±59; hour limits and combinations yielding maximum permitted offset.
CASES: Error: minutes outside valid range; offsets beyond supported daily maximum.
RISKS: Exact valid hour bounds and sign semantics are not supplied; derive only from exposed behavior/trigger.