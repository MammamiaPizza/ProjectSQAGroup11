TARGETS: createFlattened, addFlattenedActiveParsers, containedParsersCount, nextToken, switchToNext, close  
ORACLES: Trigger testInitialized expects contained parser count 2; buggy result is 3  
CASES: Flatten two parsers when one is already initialized/has a current token  
CASES: Verify flattened sequence excludes duplicate active parser inclusion and reports count 2  
CASES: Exercise nextToken across parser boundary after initialization  
RISKS: Active-parser flattening may duplicate a parser or mis-set _nextParser  
RISKS: No method bodies or trigger-test setup provided; token-level expectations are unavailable