TARGETS: DateTimeFormatter.parseInto(ReadWritableInstant,String,int) for partial month/month-day text.  
ORACLES: Trigger assertions: parsed fields retain the base instant's year when no year is parsed.  
ORACLES: Preserve base time and zone; month-only selects correct first/last day behavior.  
CASES: Base year 2004 with month-only parsing at start/end dates; expect year 2004, not 2000.  
CASES: Parse positions at start/end year contexts for month-only text, including January and December.  
CASES: Month-day "02-29" with base year 2004; retain leap-day/year and time/zone.  
CASES: Month-day Feb 29 with configured default year; expected source is truncated for that trigger.  
RISKS: Parsing may overwrite unspecified year with formatter default year (2000) instead of base year.  
RISKS: Context omits exact formatter patterns, input strings, and full default-year Feb-29 expected result.