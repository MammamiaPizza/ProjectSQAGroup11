TARGETS: QueryParser parsing of :matchText and chained :matchText:first-child selectors.  
TARGETS: Evaluator behavior for pseudo text elements; PseudoTextElement participation in selection.  
ORACLES: SelectorTest::splitOnBr and SelectorTest::textAsElements define expected selection results.  
CASES: Select `p:matchText` over paragraph content split by `<br>`.  
CASES: Select `p:matchText:first-child` to verify pseudo text elements support child position.  
RISKS: Current parser rejects `:matchText`, so chained pseudo selectors cannot be evaluated.  
RISKS: Context omits PseudoTextElement API and exact expected node/text assertions.