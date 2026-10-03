TARGETS: Lexer.nextToken escape handling; isEscape and readCharacter behavior through CSV parsing.  
ORACLES: Existing trigger assertions in CSVLexerTest and CSVParserTest define expected tokens/records.  
CASES: Escaped MySQL null value; escaped character tokenization.  
CASES: Backslash escaping within quoted text, preserving the escaped backslash/quote content.  
RISKS: Lexer is abstract/package-private; concrete lexer configuration and exact CSVFormat settings are not provided.