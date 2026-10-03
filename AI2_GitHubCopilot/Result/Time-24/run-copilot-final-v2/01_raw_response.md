TARGETS: computeMillis(boolean,String) resolves weekyear, monthOfYear, weekOfWeekyear, year;
saveField(DateTimeFieldType,int); sort()
TARGETS: saveField(DateTimeField,int), saveField(DateTimeFieldType,String,Locale); private
saveField(SavedField); resetFields clears saved state
ORACLES: Expected from failure assertions: 2010-01-04, 2011-01-03, 2012-01-02, 2016-01-04; validate
using ISOChronology.getDateTimeMillis()
ORACLES: For weekyear+month+week pattern, compute via ISO weekyear, monthOfYear, weekOfWeekyear ->
dayOfWeek=Monday, then millis
CASES: weekyear=2010 month=1 week=1; year=2010 month=1 week=1; weekyear=2011 month=1 week=1;
weekyear=2012 month=1 week=1; year=2016 month=1 week=1
CASES: weekyear=2008 month=1 week=1 (bug: expected 2008-01-? but got 2008-12-29); year=2010 month=12
week=1 (cross year)
CASES: pivotYear=2050, two-digit weekyear "39" -> 2039; null zone; default ISOChronology;
resetFields true; save/restore state
CASES: Errors: null chronology, null fieldType, negative values; conflict when both year and
weekyear saved; computeMillis with null text
RISKS: Only failing triggers; internal SavedField order may misorder fields; pivotYear default may
mask incorrect year interpretation
RISKS: Weekyear date computation relies on ISOChronology which not fully exposed; need to infer
expected from known correct DateTimeFormatter behavior