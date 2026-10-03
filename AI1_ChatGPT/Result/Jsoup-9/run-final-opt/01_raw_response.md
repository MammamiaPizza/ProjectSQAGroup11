TARGETS: Entities.unescape(String), especially named entity decoding for &frac34;  
ORACLES: Trigger assertion expects &frac34; to unescape as ¾  
CASES: Mixed text with Unicode and entities, matching trigger input/output  
CASES: Named entity &frac34; at end and adjacent to surrounding text  
CASES: Verify existing decoded entities such as &amp; remain correct in mixed input  
RISKS: unescape pattern permits optional semicolon; behavior for malformed entities is unspecified  
RISKS: Only failing assertion is provided; no broader entity-decoding specification is available