TARGETS: TypedScopeCreator.createScope; local-scope handling of namespaced and collected function stubs  
ORACLES: Existing TypedScopeCreatorTest trigger assertions are the only stated expected-result source  
CASES: Namespaced function stub declared in a local/function scope  
CASES: Collected function stub declared in a local/function scope  
RISKS: Relevant scope-builder methods are private; test through public scope creation behavior  
RISKS: No source body or assertion details supplied; do not infer slot/type semantics beyond trigger tests