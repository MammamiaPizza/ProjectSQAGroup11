TARGETS: ISO8601Utils.parse(String, ParsePosition), especially timezone-offset parsing.  
ORACLES: Trigger expects Date deserialization to accept "1970-01-01T01:00:00+01".  
CASES: Parse hour-only positive offset +01; verify ParsePosition advancement and resulting instant.  
CASES: Compare +01 with equivalent explicit offset only if accepted by current parser behavior.  
CASES: Exercise normal UTC/Z and signed offsets with hour/minute components.  
CASES: Boundary offsets and malformed/truncated timezone suffixes should retain ParseException behavior.  
RISKS: Parse is public but utility behavior is only evidenced by the failing adapter test.  
RISKS: No fixed expected Date value or complete offset-format specification is provided.