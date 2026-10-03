TARGETS: HtmlTreeBuilder.process(Token) handling of known empty blocks and subsequent text/tags  
ORACLES: HtmlParserTest::handlesKnownEmptyBlocks expected serialized HTML  
CASES: Normal: script followed by div/img, empty a/i/foo, non-empty foo, hr and text  
CASES: Boundary: self-closing versus non-self-closing known empty tags in the same input  
ORACLES: Tags after script must become elements, not escaped character text  
RISKS: HtmlTreeBuilder methods are private/package-local; test via public parser behavior  
RISKS: Context provides only one trigger and no alternate-version comparison