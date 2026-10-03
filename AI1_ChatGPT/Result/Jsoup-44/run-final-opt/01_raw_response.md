TARGETS: TreeBuilder token processing: runParser, processStartTag, processEndTag, currentElement.  
ORACLES: HtmlParserTest::testInvalidTableContents requires search text to follow its comment.  
CASES: Parse invalid table content containing comment and search text; assert resulting node order.  
CASES: Exercise start/end-tag handling around invalid table content and stack/current-element updates.  
RISKS: TreeBuilder is abstract; behavior must be tested through the available HTML parser path.  
RISKS: No full input/expected DOM or modified-code details are provided beyond the trigger assertion.