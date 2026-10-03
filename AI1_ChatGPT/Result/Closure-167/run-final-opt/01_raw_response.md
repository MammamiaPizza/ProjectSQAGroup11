TARGETS: SemanticReverseAbstractInterpreter.apply/caseIn; instanceof restriction visitors.  
TARGETS: JSType restriction/boolean-related behavior exercised by JSTypeTest.  
ORACLES: Existing triggers: expected warnings in testIssue783 and testMissingProperty20.  
ORACLES: JSTypeTest::testRestrictedTypeGivenToBoolean expected type rendering "??".  
CASES: `in` property checks with missing properties should produce the existing warning behavior.  
CASES: instanceof true/false narrowing across unknown, object, union, and function types.  
CASES: Boolean restriction of restricted/empty types, including preservation of expected type.  
RISKS: APIs and exact warning/type assertions are truncated; derive expectations only from existing tests.