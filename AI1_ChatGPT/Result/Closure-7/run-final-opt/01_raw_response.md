TARGETS: RestrictByTypeOfResultVisitor variants, matchesExpectation, getNativeTypeForTypeOf.  
ORACLES: Existing trigger assertions expect (Object|boolean|number|string).  
CASES: typeof refinement for goog.isFunction2 and Semantic typeof3 paths.  
CASES: Verify true/false typeof restrictions preserve the expected union members.  
RISKS: Protected/private visitor behavior may require exercising via existing interpreter subclasses.  
RISKS: Context lacks method bodies and exact input AST/scope setup; derive only from trigger tests.