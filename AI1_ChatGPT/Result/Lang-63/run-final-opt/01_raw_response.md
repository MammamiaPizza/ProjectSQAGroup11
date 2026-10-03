TARGETS: formatPeriod(long,long,String), reduceAndCorrect(Calendar,Calendar,int,int) date-field correction  
TARGETS: formatDuration* only if exercised as related public formatting behavior  
ORACLES: Existing testJiraLang281 expects "09" for its supplied formatPeriod scenario  
ORACLES: Public formatPeriod/formatPeriodISO contracts and existing DurationFormatUtilsTest assertions  
CASES: Reproduce testJiraLang281; assert corrected field is "09", not "-2"  
CASES: Periods crossing calendar/month boundaries; formats selecting corrected fields  
CASES: Equal start/end and adjacent boundary timestamps with zero-padding enabled by format semantics  
RISKS: Calendar locale/time-zone/default-time dependencies may affect period expectations  
RISKS: Context lacks test inputs and intended behavior beyond the reported "09" versus "-2" failure