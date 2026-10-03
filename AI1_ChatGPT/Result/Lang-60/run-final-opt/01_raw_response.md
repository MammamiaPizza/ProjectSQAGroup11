TARGETS: StrBuilder.contains(char), especially scanning only logical content (size), not buffer capacity.  
ORACLES: LANG-295 trigger assertion and StrBuilderTest::testLang295 define the expected boundary behavior.  
CASES: Empty builder and absent char must return false, including when internal capacity exceeds length.  
CASES: Present char at first/last logical positions returns true; appended content remains searchable.  
RISKS: Internal buffer is protected but capacity can exceed size; stale/uninitialized slots may cause false positives.  
RISKS: Context lacks the full existing test body and implementation; avoid assuming undocumented exception behavior.