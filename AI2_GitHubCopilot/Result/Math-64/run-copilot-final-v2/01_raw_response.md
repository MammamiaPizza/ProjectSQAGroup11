TARGETS: LevenbergMarquardtOptimizer.doOptimize (convergence), qrDecomposition (rank threshold)
ORACLES: Expected Minpack reference values 0.2578199266368004 and 11.41300466147456 from test
failures
CASES: Default-param run (Jennrich/Freudenstein); boundary step bound factor; extreme tolerances;
zero orthoTolerance
RISKS: Internal convergence logic hidden; correctness depends on QR rank threshold; fragile numeric
assertions may pass with tolerance