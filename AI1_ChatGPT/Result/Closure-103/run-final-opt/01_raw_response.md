TARGETS: ControlFlowAnalysis mayThrowException/exception edges for INSTANCEOF expressions.  
TARGETS: DisambiguateProperties property type collection/renaming across supertype-subtype references.  
ORACLES: Existing trigger assertions: INSTANCEOF has cross edges; throwing INSTANCEOF does not mark code unreachable.  
ORACLES: Existing DisambiguatePropertiesTest assertion for supertype reference of subtype property.  
CASES: INSTANCEOF normal expression and INSTANCEOF whose evaluation can throw, inside reachable statements.  
CASES: Exception-handler and no-handler contexts; verify CFG reachability/cross-edge behavior through existing tests.  
CASES: Property declared on subtype, referenced through supertype; verify disambiguation outcome via test harness.  
RISKS: Private CFG helpers require compiler AST/test utilities rather than direct unit calls.  
RISKS: Available context omits exact expected renamed property strings and full type-system behavior.