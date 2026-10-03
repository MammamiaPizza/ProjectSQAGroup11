TARGETS: DateTimeZone.convertLocalToUTC(long, boolean) around Europe/London 2011-10-30 overlap  
TARGETS: DateTimeZone.getOffsetFromLocal(long) and adjustOffset(long, boolean) at cutover  
ORACLES: Trigger expects local 2011-10-30T01:15 London to retain +01:00, not UTC  
ORACLES: Assert resulting offset/instant behavior against documented trigger comparison  
CASES: Europe/London local 01:15 during 2011-10-30 autumn overlap  
CASES: Times immediately before/after the overlap and both earlier/later offset choices  
CASES: strict and non-strict local-to-UTC conversion during overlap  
RISKS: Ambiguous local time may resolve to the wrong UTC occurrence/offset  
RISKS: Context provides no source diff or broader expected behavior beyond trigger failure