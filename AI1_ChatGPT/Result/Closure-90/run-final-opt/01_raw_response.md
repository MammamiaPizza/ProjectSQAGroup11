TARGETS: FunctionTypeBuilder builds types from JSDoc, including typedef/constructor/interface relationships.
TARGETS: FunctionType exposes call parameters, return type, prototype, instance type, and implemented interfaces.
ORACLES: TypeCheckTest::testBackwardsTypedefUse8 expects no unexpected warnings.
ORACLES: TypeCheckTest::testBackwardsTypedefUse9 expects the formal/actual parameter mismatch diagnostic.
CASES: Backwards typedef references used in function parameter types; verify warning presence versus absence.
CASES: Compare calls whose actual argument conflicts with a typedef-resolved formal parameter type.
RISKS: Builder and FunctionType interactions may alter deferred typedef resolution or diagnostic timing.
RISKS: Available context omits full changed code and exact JSDoc/test inputs; derive expectations only from triggers.