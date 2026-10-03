TARGETS: InvocationMatcher.matches and captureArgumentsFrom argument/matcher iteration on differing counts  
ORACLES: Trigger tests specify no ArrayIndexOutOfBoundsException for count-mismatched and empty-vararg calls  
CASES: Capture matcher(s) with invocation arguments count different from matcher count  
CASES: Match an invocation with an empty vararg array  
CASES: Normal equal-count arguments still match and capture through existing Matcher behavior  
RISKS: Available context lacks Matcher/Invocation contracts and exact expected match/capture results