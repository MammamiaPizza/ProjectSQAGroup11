TARGETS: TreeBuilderState.process(Token, TreeBuilder), especially character-token handling of U+0000.
ORACLES: Existing trigger ParserTest::handles0CharacterAsText defines expected parsing behavior.
CASES: Parse text containing a standalone NUL character; assert it is retained as text, not discarded.
CASES: Parse NUL adjacent to ordinary text to verify ordering and preservation.
RISKS: TreeBuilderState methods are private/package-internal; test through public parser behavior.
RISKS: No full expected DOM/string assertion is provided beyond the trigger failure context.