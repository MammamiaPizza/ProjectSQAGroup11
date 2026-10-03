TARGETS: PeriodFormatterBuilder FieldFormatter parsing, especially appendSeconds() and numeric digit handling.  
ORACLES: testBug2495455 requires "PT1003199059S" not fail as malformed input.  
ORACLES: Expected acceptance source is bug report 64 and the named trigger test.  
CASES: ISO-like period text with a 10-digit seconds value and trailing "S".  
CASES: Ordinary shorter seconds values to detect regressions in numeric parsing.  
RISKS: Trigger setup and the expected parsed Period value are not provided.