TARGETS: DateTimeZone.adjustOffset(long, boolean) at overlap/cutover transitions.  
ORACLES: Trigger expects 2012-02-25T23:15 local with -03:00, not -02:00.  
ORACLES: Compare resulting instant/offset using DateTimeZone public offset and conversion behavior.  
CASES: Brazil-like 2012-02-25 overlap: choose earlier and later offset occurrences.  
CASES: Verify preserved local time while adjustOffset selects requested overlap side.  
CASES: Normal instant outside overlap: earlierOrLater should not alter instant.  
CASES: Exact transition boundary and nearby milliseconds around the overlap.  
RISKS: Zone ID/setup and transition data are not provided; derive only from available provider data.