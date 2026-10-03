TARGETS: AbstractLeastSquaresOptimizer metrics: getRMS, getChiSquare, getCovariances, guessParametersErrors.  
TARGETS: Evaluation/iteration limits and counters; convergence-checker setter/getter.  
ORACLES: Existing testCircleFitting expects 0.004; failure observed 0.0019737107108948474.  
ORACLES: Metric/error results derive from optimizer state after concrete doOptimize execution.  
CASES: Circle-fitting optimization through LevenbergMarquardtOptimizer; verify reported parameter error.  
CASES: Normal residual/weight inputs; RMS and chi-square consistency after optimization.  
CASES: Boundary rows == cols for guessParametersErrors division by rows-cols.  
RISKS: Target is abstract; protected update methods/state require a concrete optimizer or test subclass.  
RISKS: No source-level changed logic or full circle-fitting fixture is provided.