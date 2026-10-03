TARGETS: Entities.escape(String, Document.OutputSettings) with charset encoder and EscapeMode maps.  
TARGETS: Entities.canEncode(CoreCharset, char, CharsetEncoder) charset representability decisions.  
ORACLES: Existing trigger DocumentTest.testShiftJisRoundtrip: serialized output must not contain '?'.  
ORACLES: CharsetEncoder is the expected-result source for non-core charset encodability.  
CASES: Shift_JIS text containing characters representable by that encoder; escape then document roundtrip.  
CASES: Mixed ASCII, named-entity characters, and Shift_JIS-representable non-ASCII characters.  
CASES: Characters not representable in Shift_JIS, verifying escaping avoids replacement output.  
RISKS: escape is package-private; tests may need org.jsoup.nodes package or Document serialization.  
RISKS: Entity mappings and exact escaping form are not provided; avoid asserting invented entity names.