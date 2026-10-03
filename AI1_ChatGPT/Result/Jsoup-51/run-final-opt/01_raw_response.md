TARGETS: CharacterReader.consumeToAny and consumeToAnySorted handling of non-ASCII characters  
TARGETS: CharacterReader.rangeEquals/cacheString behavior where parser token text is derived  
ORACLES: HtmlParserTest::testSupportsNonAsciiTags expects parsed text "Yes"  
CASES: Parse HTML containing non-ASCII tag names and verify contained text is retained  
CASES: Exercise consume-to-delimiter paths with non-ASCII input before an ASCII delimiter  
CASES: Boundary inputs: non-ASCII character at start, end, and immediately before delimiter  
RISKS: CharacterReader is package-private; tests may need parser-level coverage  
RISKS: No detailed contract or expected results are provided for CharacterReader methods alone