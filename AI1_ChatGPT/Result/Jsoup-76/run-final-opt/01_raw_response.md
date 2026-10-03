TARGETS: HtmlTreeBuilderState.process(Token, HtmlTreeBuilder) behavior while parsing pre content  
ORACLES: HtmlParserTest::preSkipsFirstNewline is the available expected-result source  
CASES: pre content beginning with one newline should skip that first newline  
CASES: pre content without an initial newline should preserve its initial content  
CASES: pre content with multiple leading newlines should distinguish the first newline  
RISKS: Token/state helpers are private; exercise behavior through HtmlParser parsing  
RISKS: Trigger assertion’s complete expected text is unavailable in the provided context