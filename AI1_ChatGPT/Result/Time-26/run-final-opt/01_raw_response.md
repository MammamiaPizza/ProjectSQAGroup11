TARGETS: ZonedChronology local field-setting across DST overlaps; select correct post-transition offset.  
TARGETS: DateTimeZone conversion/offset behavior at cutovers; LenientDateTimeField.set local-to-UTC handling.  
ORACLES: Trigger assertions provide expected rendered local timestamps and offsets at named DST transitions.  
CASES: Set second/minute/millis/hour within autumn overlap; preserve requested local fields and expected offset.  
CASES: Paris summer 2005-10-31, New York winter 2007-11-04, US Central 2008-11-02, NSW 2008-04-06.  
CASES: Mock-zone minute change validates non-hour offset transitions (+01:00 versus +00:30).  
RISKS: Ambiguous local times have two valid offsets; assertions must target the documented trigger direction.  
RISKS: Context omits complete method bodies and truncated failure details; derive expectations only from supplied triggers.