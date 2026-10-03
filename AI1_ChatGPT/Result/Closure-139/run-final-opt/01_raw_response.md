TARGETS: Normalize.process; NormalizeStatements moves named functions, splits vars, normalizes labels.  
TARGETS: removeDuplicateDeclarations and DuplicateDeclarationHandler; replaceVarWithAssignment behavior.  
ORACLES: Existing NormalizeTest trigger assertions and compiler diagnostic count/messages.  
ORACLES: No JSC_VAR_MULTIPLY_DECLARED_ERROR for duplicate declaration scenario involving f.  
CASES: Function declarations in statement/block contexts requiring normalization or movement.  
CASES: Repeated var declarations, especially declaration plus function named f in one scope.  
CASES: Function movement ordering with surrounding statements and nested function bodies.  
CASES: Multi-name var declarations and initialized duplicates requiring assignment replacement.  
RISKS: Exact normalized AST/source output and intended ordering are not provided in this context.