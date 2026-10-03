TARGETS: Entities.unescape(String/Writer) numeric entity parsing and overflow handling.  
ORACLES: Trigger expects "&#12345678;" unchanged, not converted to a truncated Unicode character.  
CASES: Normal named and valid numeric entities; decimal numeric entity at ordinary valid range.  
CASES: Boundary numeric values around character-range limits and very large decimal values.  
CASES: Malformed/unterminated numeric entities and nonnumeric numeric references remain safely handled.  
RISKS: Only trigger supplies overflow semantics; exact behavior for other invalid references is unspecified.