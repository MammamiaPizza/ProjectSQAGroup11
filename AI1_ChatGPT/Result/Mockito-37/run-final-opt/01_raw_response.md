TARGETS: AnswersValidator.validate(Answer<?>, Invocation), especially real-method calls on interface methods.  
ORACLES: Trigger tests expect fast validation failure; Reporter-generated failure is the available expected source.  
CASES: CALLS_REAL_METHODS/real-method answer with an interface invocation should fail validation.  
CASES: Spy configured on an interface, invoking real method, should fail fast.  
CASES: Preserve validation paths for DoesNothing, Returns, and ThrowsException answers.  
RISKS: Invocation/Answer concrete APIs and exact exception/message are not provided; avoid asserting invented details.