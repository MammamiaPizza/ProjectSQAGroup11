TARGETS: LocalDate.fromDateFields(Date), fromCalendarFields(Calendar) preserve proleptic years <=0.  
TARGETS: LocalDateTime.fromDateFields(Date), fromCalendarFields(Calendar) preserve date and time fields.  
ORACLES: Trigger expectations: year 0 -> "0000-02-03"; year -2 -> "-0002-02-03".  
ORACLES: LocalDateTime trigger expectations retain "T04:05:06.007" with those year values.  
CASES: Date and Calendar inputs representing 0000-02-03 and -0002-02-03.  
CASES: Verify both factories for LocalDate and LocalDateTime, including exact string results.  
RISKS: Legacy Date/Calendar era/year conventions may require carefully constructed before-CE inputs.  
RISKS: Context omits factory implementation and complete Calendar/Date setup from existing tests.