TARGETS: HtmlTreeBuilder.process token handling; insertion-mode transitions around table cells (th/td).  
ORACLES: Existing HtmlParserTest::testReinsertionModeForThCelss assertion (expected 1, observed 5).  
CASES: Parse table markup with th/td end/start tokens that require reinsertion-mode recovery.  
CASES: Verify resulting element count/tree placement after cell closure and subsequent tokens.  
CASES: Boundary sequences: nested/adjacent th/td, missing end tags, and table-context text tokens.  
RISKS: Only private HtmlTreeBuilder methods are listed; test through public parser behavior.  
RISKS: Exact triggering HTML and asserted object/count are not provided; derive expectations only from known tests/spec.