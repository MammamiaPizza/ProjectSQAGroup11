TARGETS: CMAESOptimizer.doOptimize/checkParameters validate inputSigma against lower/upper-bound range.  
ORACLES: Trigger expects NumberIsTooLargeException, not MathIllegalStateException.  
CASES: Bounded optimization with an inputSigma value greater than its coordinate range.  
CASES: inputSigma equal to coordinate range as the validation boundary; smaller sigma normal case.  
RISKS: Public optimize invocation/setup signatures are not provided; use only available project test patterns.