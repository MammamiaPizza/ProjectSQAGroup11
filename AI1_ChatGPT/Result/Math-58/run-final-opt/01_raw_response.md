TARGETS: GaussianFitter.fit(); ParameterGuesser.guess(), basicGuess(), findMaxY(), and point sorting comparator  
ORACLES: Trigger testMath519 should not fail with NotStrictlyPositiveException from a negative Gaussian width  
CASES: fit() with observations used by testMath519; verify returned parameters are accepted by Gaussian.Parametric  
CASES: ParameterGuesser.guess() on unsorted observations and peak/interpolation boundary positions  
CASES: Width estimation where crossing points lie on either side of the maximum x value  
RISKS: Available context omits testMath519 data and exact expected fitted parameter values  
RISKS: Do not assume optimizer convergence, numerical tolerances, or behavior beyond the reported exception