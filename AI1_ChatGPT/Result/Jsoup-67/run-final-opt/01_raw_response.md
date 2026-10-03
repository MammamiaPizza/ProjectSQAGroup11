TARGETS: HtmlTreeBuilder parsing stack handling exercised by HtmlParserTest::handlesDeepStack.  
ORACLES: Existing trigger assertion/result is the only stated expected-behavior source.  
CASES: Deeply nested HTML that stresses open-element stack growth and completion.  
CASES: Normal shallow nesting baseline versus deep nesting, checking parse completion/tree result via public parser API.  
RISKS: HtmlTreeBuilder methods are mostly protected/private; test through available parser-facing behavior.  
RISKS: No precise nesting depth, expected DOM, or failure mechanism is provided; avoid inferred semantics.