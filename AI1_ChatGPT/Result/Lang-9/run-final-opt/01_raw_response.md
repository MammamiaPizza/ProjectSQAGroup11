TARGETS: FastDateParser.parse(String) and parse(String, ParsePosition) numeric-field token boundaries.  
TARGETS: Quoted literal followed by numeric day pattern, e.g. pattern "d'd'" with input "d3".  
ORACLES: Trigger assertions require rejecting "d3" for pattern "d'd'" rather than returning Jan 2, 1970.  
ORACLES: parse(String) failure is indicated by ParseException; ParsePosition parse result should be null on no match.  
CASES: Valid numeric day followed by its required quoted literal (e.g. "3d") parses successfully.  
CASES: Literal before digit and digit-only/missing-literal inputs must not be accepted by numeric matching.  
CASES: Boundary numeric widths adjacent to literals: one digit versus multiple Unicode decimal digits.  
RISKS: Constructor is protected; tests may need existing factory/subclass/package access.  
RISKS: No implementation diff or broader grammar semantics supplied; limit expectations to LANG-832 triggers.