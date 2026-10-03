TARGETS: JSType subtype/equality behavior used by empty FunctionType and TypeCheck issue 301 paths.  
ORACLES: Existing trigger assertions: issue 301 must emit a warning; empty-function test must pass.  
CASES: Empty function types: compare subtype/equivalence and equality-related type-pair outcomes.  
CASES: Type-check code reproducing issue 301; assert diagnostic warning presence, not unspecified text/count.  
RISKS: JSType is abstract; construct types through existing project factories/helpers only.  
RISKS: Context lacks patch and trigger source details; avoid inferring exact subtype/equality semantics.