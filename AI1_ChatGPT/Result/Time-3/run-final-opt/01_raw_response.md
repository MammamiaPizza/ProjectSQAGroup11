TARGETS: MutableDateTime addYears, addDays, addWeeks, addMonths, and add(DurationFieldType,int).
ORACLES: Trigger assertions require 2011-10-30T02:30:00.000+01:00 after add-zero in winter DST overlap.
CASES: Construct the overlap instant and call each target with amount 0; assert exact string/offset.
CASES: Cover DurationFieldType-based zero add using the type exercised by the supplied trigger.
RISKS: Ambiguous local DST-overlap time may preserve/select the incorrect +02:00 offset.
RISKS: Context omits exact zone, construction inputs, and DurationFieldType used by trigger tests.