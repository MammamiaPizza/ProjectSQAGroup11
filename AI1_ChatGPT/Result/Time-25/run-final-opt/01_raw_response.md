TARGETS: DateTimeZone.getOffsetFromLocal(long); convertLocalToUTC(long,boolean) for Moscow autumn overlap.  
ORACLES: Trigger expectations: 2007-10-28T02:00/02:30 local Moscow resolves to +04:00, not +03:00.  
CASES: DateTimeZone.forID("Europe/Moscow"), local 2007-10-28T02:00:00.000 and 02:30:00.000.  
CASES: Assert DateTime construction preserves local fields and reports +04:00 during overlap.  
CASES: Boundary minutes around overlap start, including 02:00 and nearby minutes.  
RISKS: Exact transition boundaries/expected offsets beyond supplied trigger values are not provided.