TARGETS: ElitisticListPopulation(int,double) and setElitismRate(double) for elitismRate validation
ORACLES: OutOfRangeException expected for elitismRate outside [0,1]; no exception for in-range
CASES: elitismRate: -0.5, 0.0, 0.5, 1.0, 1.5; populationLimit: 0, 1, 10; setElitismRate with -0.1,
0.3, 1.2
RISKS: Only constructors and setElitismRate relevant; nextGeneration not directly testable