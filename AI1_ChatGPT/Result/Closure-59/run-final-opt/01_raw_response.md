TARGETS: Compiler initOptions, compile/check pipeline, and warning/error retrieval for global-this diagnostics.  
ORACLES: Trigger expects zero warnings and zero errors when checkGlobalThis is off.  
CASES: Compile source involving global `this` with the relevant option disabled; assert getWarningCount/getErrorCount are 0.  
CASES: Verify getWarnings/getErrors are empty after compilation under the disabled setting.  
RISKS: Exact CompilerOptions setup, source fixture, and diagnostic identity are not provided.  
RISKS: No alternate version may be used; derive expectations only from trigger and stated summary.