TARGETS: DateUtils.round(Date/Calendar/Object,int), especially Calendar.MINUTE rounding via modify  
ORACLES: Trigger expects Mon Jul 02 08:09:00 PDT 2007, not 08:01:00, for minute round-up  
CASES: Reproduce trigger timestamp; assert minute-round result preserves correct higher hour/minute fields  
CASES: Boundary minute values below/at/above round threshold; verify seconds and milliseconds clear  
CASES: Compare Date and Calendar round overloads for the same instant and Calendar.MINUTE field  
RISKS: Exact trigger input and timezone setup are absent; derive expected values only from supplied trigger evidence  
RISKS: Do not infer behavior for unsupported fields, nulls, parsing, or iterator APIs from this context