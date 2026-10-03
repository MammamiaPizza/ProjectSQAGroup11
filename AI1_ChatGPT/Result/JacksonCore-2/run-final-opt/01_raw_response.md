TARGETS: ReaderBasedJsonParser and UTF8StreamJsonParser numeric-token parsing/error handling.  
ORACLES: Trigger tests require malformed numbers to throw, not return VALUE_NUMBER_INT.  
CASES: Exercise mangled numeric inputs through character-based parser construction.  
CASES: Exercise identical mangled numeric inputs through UTF-8 byte-stream parser construction.  
RISKS: Exact malformed-number strings and intended exception type/message are not provided.  
RISKS: Available context does not expose modified method bodies or parser factory setup.