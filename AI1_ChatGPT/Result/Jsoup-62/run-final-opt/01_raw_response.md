TARGETS: HtmlTreeBuilderState token processing for case-sensitive start/end-tag matching  
ORACLES: HtmlParserTest::caseSensitiveParseTree expected parse-tree string from trigger  
CASES: `<r><X>A</X><y>B</y></r>` keeps `X` and `y` as sibling elements  
CASES: Mixed-case end tag `</X>` must close matching mixed-case start tag `<X>`  
CASES: Verify following `<y>` is not nested inside `<X>` after `</X>`  
RISKS: Only trigger and state enum signatures are available; no broader behavior inferred