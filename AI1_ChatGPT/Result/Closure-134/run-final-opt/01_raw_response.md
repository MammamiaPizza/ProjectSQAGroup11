TARGETS: AmbiguateProperties.process property renaming with @implements and @extends types.
TARGETS: TypedScopeCreator.createScope declarations/type resolution affecting TypeCheck diagnostics.
ORACLES: AmbiguatePropertiesTest.testImplementsAndExtends existing assertion behavior.
ORACLES: TypeCheckTest.testIssue86 requires a warning.
CASES: Interface/class inheritance property relationships during ambiguation.
CASES: Scope creation input that must preserve the warning expected by issue86.
RISKS: Available signatures omit modified-code details and exact expected renamed output.
RISKS: Warning type/message and issue86 source pattern are not provided.