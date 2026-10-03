TARGETS: BrentSolver.solve(min,max) endpoint-root selection; solve(min,max,initial) if applicable.  
ORACLES: Trigger testRootEndpoints expects 3.141592653589793, not 1.2246467991473532E-16.  
CASES: Function values zero at both interval endpoints; verify expected endpoint is returned.  
CASES: Root only at min; root only at max; ordinary interior root with a valid bracket.  
CASES: Initial value at/near an endpoint and near an interior root for three-argument solve.  
RISKS: Endpoint equality and floating-point near-zero handling can select the wrong endpoint.  
RISKS: Context lacks function inputs, tolerance settings, and specified invalid-interval/error behavior.