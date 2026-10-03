TARGETS: TypedScopeCreator.DiscoverEnumsAndTypedefs.visit enum declaration discovery/validation.
TARGETS: Enum initializer handling and diagnostic "enum initializer must be an object literal or an enum".
ORACLES: TypeCheckTest.testIssue726 expects a warning for the issue-726 input.
ORACLES: Diagnostic text in TypedScopeCreator is the available expected-result source.
CASES: Enum initialized with an object literal; ensure it is accepted without this diagnostic.
CASES: Enum initialized from another enum; ensure it is accepted without this diagnostic.
CASES: Enum initialized with a non-object/non-enum expression; expect the enum-initializer warning.
RISKS: TypedScopeCreator is package-private; tests may need compiler-level integration setup.
RISKS: Provided context lacks the trigger JavaScript input and diagnostic type/group details.