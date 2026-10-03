TARGETS: HtmlTreeBuilderState.process(Token, HtmlTreeBuilder) handling of anchor start/end tags  
ORACLES: HtmlParserTest::handlesUnclosedAnchors expected serialized anchor structure  
CASES: Unclosed `<a>` followed by another anchor; verify prior anchor closes before next link  
CASES: Nested/adjacent anchors with text; preserve link text and resulting `</a>` placement  
RISKS: Enum-private state transitions are not directly callable; test through parser behavior  
RISKS: Expected behavior is limited to the trigger assertion; no alternate-version comparison available