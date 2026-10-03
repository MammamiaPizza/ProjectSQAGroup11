TARGETS: PatternBuilder.withPattern(String), create(), reset() option construction/state behavior  
ORACLES: Bug27575Test::testRequiredOptions comparison expects required option list "[-h]"  
CASES: Build the trigger’s required-option pattern; parse its failing input and verify "-h" is reported  
CASES: Include competing "-c <arg>" option to ensure it is not reported as the missing required option  
CASES: Reuse one PatternBuilder across create/reset to check option-state isolation  
RISKS: No parser API, exact pattern text, or broader expected semantics are provided in this context