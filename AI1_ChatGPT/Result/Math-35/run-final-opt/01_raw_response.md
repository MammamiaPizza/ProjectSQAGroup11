TARGETS: ElitisticListPopulation(int,double) and setElitismRate(double) validate elitismRate bounds.
ORACLES: Trigger tests require OutOfRangeException for constructor elitism rates too low or too high.
CASES: Construct with negative elitismRate and elitismRate greater than 1; assert OutOfRangeException.
CASES: Exercise both population-limit constructor context and chromosome-list constructor if accessible in source.
CASES: Check boundary rates 0 and 1 are accepted; getElitismRate returns the supplied valid rate.
RISKS: Provided API list omits chromosome-list constructor; inspect source/tests for its exact signature.
RISKS: No expected exception details or setElitismRate behavior are explicitly supplied beyond validation implication.