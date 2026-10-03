TARGETS: FastDatePrinter.format(Calendar) and format(Calendar,StringBuffer) must respect the Calendar timezone.  
ORACLES: Trigger expectation: pattern output contains "2:43PM ICT", not "2:43PM PST", for supplied Calendar.  
CASES: Calendar timezone differing from printer timezone; format(Calendar) with timezone-name pattern (z).  
CASES: Verify both direct String result and StringBuffer overload preserve Calendar timezone.  
RISKS: Constructor is protected; tests may need an existing public factory/wrapper in project context.  
RISKS: No broader timezone-name, locale, DST, or numeric-offset expectations are specified.