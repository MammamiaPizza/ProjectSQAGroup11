TARGETS: processProvideCall/ProvidedName module placement for goog.provide in independent modules.  
TARGETS: processRequireCall and verifyProvide/verifyArgument diagnostics and namespace handling.  
ORACLES: Existing trigger test assertion and compiler AST/output behavior are the only stated expected source.  
CASES: Independent modules providing related namespaces; ensure declarations remain valid per module ordering.  
CASES: Repeated provide, provide after require, and nested namespace provide where supported by existing tests.  
CASES: Invalid/non-string provide or require arguments should follow verifyArgument diagnostics.  
RISKS: Most relevant methods are private; tests must exercise them through compiler processing.  
RISKS: Context gives no exact expected transformed source or diagnostic text beyond one inherits message.