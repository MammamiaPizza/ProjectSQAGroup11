TARGETS: DateTimeFormatter.parseInto(ReadWritableInstant,String,int) for month-day pattern (e.g., "M
d").
ORACLES: If instant's year is leap (2000,2004), parse "2 29" must succeed without exception and set
month=2,day=29.
ORACLES: If instant's year is non-leap (2001), parse "2 29" must throw IllegalFieldValueException
(Value 29 for dayOfMonth).
CASES: Normal: parse "2 29" into DateTime(2000-01-01T00:00:00Z) → success, month=2, day=29.
CASES: Normal: parse "2 29" into DateTime(2004-12-31T23:59:59.999 NewYork/Tokyo) → success, month=2,
day=29.
CASES: Normal: parse "2 28" into any year (leap or non-leap) → always succeed, month=2, day=28.
CASES: Boundary: parse "2 29" into DateTime(2001-01-01) → exception; century non-leap year 2100 →
exception.
CASES: Boundary: parse "2 29" into instant with year 0 or negative leap year → verify behavior
(likely same rules).
CASES: Error: parse invalid "2 30", "2 0", "13 1" → expect IllegalFieldValueException.
RISKS: Default-year handling when instant year is unset (e.g., MutableDateTime default) is unclear;
may still fail.
RISKS: Timezone transitions (DST) may shift instant causing off-by-one; pattern inferred from
trigger strings, not from code.