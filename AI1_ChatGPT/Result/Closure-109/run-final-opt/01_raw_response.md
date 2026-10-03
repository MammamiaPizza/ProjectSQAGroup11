TARGETS: JsDocInfoParser structural @constructor parsing and type-expression warning behavior.  
ORACLES: Existing triggers require no extra "Bad type annotation...syntax error" warning.  
CASES: Structural constructor form exercised by testStructuralConstructor2.  
CASES: Structural constructor form exercised by testStructuralConstructor3.  
CASES: Valid constructor/type syntax must parse without syntax-warning diagnostics.  
RISKS: Relevant parser entry points are mostly private; use existing parser-test harness/API.  
RISKS: Exact JSDoc inputs and intended AST/info assertions are not provided in this context.