TARGETS: FastDateFormat.getInstance(pattern,tz,locale) and format(Date/long/Calendar) timezone conversion.  
ORACLES: LANG-538 trigger expects 2009-10-16T16:42:16.000Z, not 08:42:16.000Z.  
CASES: Format trigger instant with "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" and UTC timezone.  
CASES: Verify Date, millis, and Calendar overloads preserve the configured formatter timezone.  
CASES: Exercise a non-UTC source Calendar against a UTC-configured formatter.  
RISKS: Timezone/default-timezone interaction is implicated; isolate and restore defaults if changed.  
RISKS: Context omits exact trigger setup and modified implementation; use only stated expected output.