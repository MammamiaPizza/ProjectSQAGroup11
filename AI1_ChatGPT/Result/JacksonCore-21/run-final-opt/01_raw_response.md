TARGETS: FilteringParserDelegate.nextToken(), _nextToken2(), buffering/path exposure, and skipChildren().  
ORACLES: Trigger assertions: filtered single match with path must emit enclosing END_OBJECT, not null.  
ORACLES: Serialized token output source: expected `{"ob":{"value":3}}`, including final outer close.  
CASES: Single matched scalar nested in objects, with `_includePath` enabled; consume through EOF.  
CASES: Assert token order includes START_OBJECT, field/path tokens, scalar 3, both END_OBJECT tokens, then null.  
CASES: Call skipChildren() while traversing the single matched/path-exposed structure; expect END_OBJECT afterward.  
CASES: Verify currentToken()/hasCurrentToken() transition after final END_OBJECT and after EOF.  
RISKS: Available context omits constructors/filter setup details and full TokenFilter behavior.