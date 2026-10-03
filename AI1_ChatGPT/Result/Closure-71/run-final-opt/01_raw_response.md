TARGETS: CheckAccessControls traversal/visit checks private property access and overriding on prototypes.  
TARGETS: process and hotSwapScript establish traversal behavior; scope entry/exit may affect access context.  
ORACLES: Existing trigger assertions/messages: private Foo.prototype property override must report an error.  
ORACLES: testNoPrivateAccessForProperties8 expects exactly one error for its private-property scenario.  
CASES: Private property declaration/access through Foo.prototype, including an overriding declaration in another type.  
CASES: Validate reported-error count and diagnostic text where existing tests specify it.  
CASES: Exercise both full process and hot-swap traversal only if existing test harness supports equivalent inputs.  
RISKS: Private access rules depend on AST/type/scope context; construct cases via existing compiler test utilities.  
RISKS: Context lacks source snippets and diagnostic constants, so exact additional boundary behavior is unspecified.