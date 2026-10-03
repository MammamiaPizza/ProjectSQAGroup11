TARGETS: Tokeniser/TokeniserState handling of unterminated textarea and title raw-text/RCDATA input  
ORACLES: ParserTest expected text excludes trailing markup after unterminated textarea/title  
CASES: Parse `<textarea>one<p>two` and assert textarea text is `one`  
CASES: Parse `<title>One<b>Two <p>Test</p>` and assert title text is `One`  
CASES: Verify normal closed textarea/title retain text before their closing tags  
RISKS: Tokeniser APIs are package-private; test via parser-level behavior if accessible  
RISKS: No modified-source diff supplied; limit assertions to reported trigger behavior