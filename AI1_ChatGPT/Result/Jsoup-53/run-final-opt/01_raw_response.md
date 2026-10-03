TARGETS: TokenQueue.chompBalanced(open, close) while parsing bracketed selector attribute values  
ORACLES: SelectorTest::attributeWithBrackets must parse `div[data='End]']` without SelectorParseException  
CASES: Balanced `[...]` content containing `]` inside single-quoted attribute values  
CASES: Equivalent double-quoted values and unquoted bracketed attribute content  
CASES: Empty input, missing close delimiter, nested delimiters, and escaped quote/delimiter handling  
RISKS: Quote state must not treat delimiters inside quotes as structural closing delimiters  
RISKS: Context lacks fixed-version behavior and direct TokenQueue expected outputs beyond the trigger