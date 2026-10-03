TARGETS: TypeInference call/property traversal and TypedScopeCreator scope/type declaration paths.  
ORACLES: Existing trigger assertions: warning presence, inferred function type string, and no NPE.  
CASES: Issue1023 input must produce the expected warning through TypeCheckTest harness behavior.  
CASES: Method-before-function declaration must infer "function (this:Window, ?): undefined", not "?".  
CASES: Interface property scenario must complete scope creation/type processing without NullPointerException.  
RISKS: Target methods are mostly private; test through compiler/type-check integration fixtures.  
RISKS: Available context omits source bodies and exact JavaScript inputs; reuse only named trigger test behavior.