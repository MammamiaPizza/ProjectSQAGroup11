TARGETS: ClosureReverseAbstractInterpreter.apply/caseObjectType/caseFunctionType for goog predicates on null  
TARGETS: TypeInference traversal/flow narrowing as exercised by TypeCheckTest Issue124 and Issue124b  
ORACLES: Existing trigger assertions: null narrowed to Array, Function, or Object for true predicate outcomes  
ORACLES: Existing TypeCheck diagnostics: Issue124 has no unexpected warnings; Issue124b comparison expects false  
CASES: goog.isArray(null), goog.isFunction(null), goog.isObject(null) under the tested branch outcome  
CASES: Issue124/124b source patterns through TypeCheck to validate inferred boolean/result diagnostics  
RISKS: Most target methods are private/package-private; tests should use existing compiler-test harnesses  
RISKS: Context omits predicate call construction and complete expected diagnostic text; rely on supplied triggers