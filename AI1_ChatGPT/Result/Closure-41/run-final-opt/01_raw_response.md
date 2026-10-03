TARGETS: FunctionTypeBuilder function-type construction and JSDoc-driven parameter type inference.  
ORACLES: Trigger tests’ warning expectations, especially Bar.prototype.add actual parameter 2 mismatch.  
CASES: Method inference with declared formal parameters and calls having compatible argument types.  
CASES: Calls with an incompatible second argument must retain the expected parameter-mismatch warning.  
CASES: Loose type checking method inference must not emit unexpected warnings.  
RISKS: Available context omits public builder entry points and complete expected diagnostics.