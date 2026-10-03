TARGETS: FilteringParserDelegate.nextToken(), _nextToken2(), buffering/path exposure, match counting.  
ORACLES: Trigger tests' asserted token/match counts; getMatchCount(), currentToken(), EOF behavior.  
CASES: Property-name matches with includePath true: single, multiple allowed/disallowed, nested paths.  
CASES: includePath false: single/multiple matches and stopping when multiple matches are disallowed.  
CASES: Array index matches with path enabled, including first and multiple selected indices.  
RISKS: Bug context is truncated; filter construction and exact expected token sequences are not provided.