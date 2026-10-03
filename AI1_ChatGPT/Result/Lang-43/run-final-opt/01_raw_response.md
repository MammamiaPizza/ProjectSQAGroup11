TARGETS: ExtendedMessageFormat.applyPattern(String), constructors, and toPattern() quote parsing behavior.  
ORACLES: Existing trigger test testEscapedQuote_LANG_477; MessageFormat-compatible pattern/toPattern semantics.  
CASES: Escaped single quotes around/near format elements; ensure construction/applyPattern terminates without OOM.  
CASES: Patterns with quoted braces, apostrophe pairs, and custom-format descriptions if registry supplied.  
CASES: Normal unquoted arguments and repeated applyPattern calls preserve expected toPattern output.  
RISKS: Infinite parsing loop in format-description/quote handling causes heap exhaustion.  
RISKS: Context lacks the trigger test pattern and expected exact output; derive only from available tests/API.