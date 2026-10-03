TARGETS: validate(Answer,Invocation); validateDoNothing; validateReturnValue; validateException
TARGETS: reject CALLS_REAL_METHODS/CallsRealMethods answer when invocation method is on an interface
ORACLES: MockitoException thrown on interface real-method calls; trigger tests expect fail-fast
CASES: normal Returns/ThrowsException/DoesNothing answers pass; interface method with real-method
answer fails
CASES: boundary void return + real-method answer on interface; null invocation/answer inputs
RISKS: listed signatures omit any validateCallsRealMethods method; dispatch logic not fully visible
RISKS: expected exception class/message unspecified by summary; interface-vs-class detection API
unknown