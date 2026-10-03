TARGETS: MultiDirectional.iterateSimplex comparator-guided reflection, expansion, and contraction choices  
ORACLES: Existing testMinimizeMaximize assertions are the expected-result source for min/max behavior  
ORACLES: Existing testMath283 completion without MaxIterationsExceededException is the convergence oracle  
CASES: Minimize and maximize runs exercising reflected versus expanded simplex selection  
CASES: MATH-283 scenario requiring progress within the configured 100-iteration limit  
CASES: Boundary comparison where reflected result is not better than the current simplex best  
RISKS: iterateSimplex is protected; tests may need coverage through public optimizer execution  
RISKS: No objective functions, optimizer invocation API, or exact numeric assertions are provided here  
RISKS: Do not derive expected coordinates or values from another program version