TARGETS: Cleaner.isValid(Document) and clean(Document) with a Whitelist  
ORACLES: isValid Javadoc: true iff no tags or attributes need removal  
ORACLES: clean Javadoc: returns a cleaned document; trigger CleanerTest::testIsValid  
CASES: Document containing only whitelist-allowed tags and attributes  
CASES: Document with a disallowed tag; expect isValid false  
CASES: Allowed tag with a disallowed attribute; expect isValid false  
CASES: Compare isValid result before/after clean for documents requiring removal  
RISKS: Whitelist configuration and concrete expected markup are not provided  
RISKS: No source/body context is available for private safe-node copying behavior