TARGETS: TypedScopeCreator.DiscoverEnumsAndTypedefs.visit – detect invalid enum init
TARGETS: TypedScopeCreator.DeferredSetType (resolve) – may emit enum-init warning
ORACLES: Warning message "enum initializer must be an object literal or an enum"
ORACLES: Verify warning via Compiler’s ErrorManager/warnings list (TypeCheckTest style)
CASES: Valid: enum with object literal init, enum with another enum
CASES: Invalid: enum init is a variable, number, string, null, or non-object literal
RISKS: Need to access ScopeCreator/Compiler warnings; no external API beyond TypedScopeCreator