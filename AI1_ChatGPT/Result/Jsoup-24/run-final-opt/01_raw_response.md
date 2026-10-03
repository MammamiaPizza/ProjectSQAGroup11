TARGETS: TokeniserState.read(Tokeniser, CharacterReader) during script parsing with comment text and quotes.  
ORACLES: HtmlParserTest::handlesQuotesInCommentsInScripts expected script text/tokenization output.  
CASES: Script containing comment-like text with quotes around fragments resembling closing </script>.  
CASES: Boundary where quoted comment content includes split or embedded </script> sequence.  
RISKS: Only TokeniserState signatures are available; exact state transitions and public parser API are not provided.