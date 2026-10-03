TARGETS: DateTimeParserBucket.computeMillis(boolean,String); SavedField ordering during weekyear/year-month-week parsing.  
ORACLES: Trigger assertions: parsed LocalDate is 2010-01-04, 2011-01-03, 2012-01-02, or 2016-01-04.  
CASES: Parse weekyear-month-week inputs for 2010, 2011, 2012; verify Monday dates above.  
CASES: Parse year-month-week inputs for 2010, 2011, 2012, 2016; verify the same expected dates.  
RISKS: Incorrect saved-field sort precedence lets weekyear/year resolve before month/week, yielding prior-year dates.  
RISKS: Context lacks exact formatter patterns/input strings and direct expected behavior for bucket state/error paths.