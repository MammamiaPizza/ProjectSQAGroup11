TARGETS: InvocationMatcher.matches, hasSimilarMethod, hasSameMethod with inherited generic polymorphic methods.  
ORACLES: Existing trigger tests' stubbing and verification assertions are the expected-result source.  
CASES: Match invocation of inherited generic method across parent/child polymorphic call signatures.  
CASES: Verify stubbing lookup and verification both recognize the intended method and arguments.  
CASES: Same method name/mock with bridge or differing reflective Method identity.  
RISKS: No source or detailed assertions provided; avoid assuming method-resolution semantics beyond triggers.