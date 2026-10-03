TARGETS: NodeUtil.getPureBooleanValue/getImpureBooleanValue behavior for AST expressions, especially boolean coercion.  
ORACLES: Existing NodeUtilTest::testGetBooleanValue expected TernaryValue; failure expects unknown, not false.  
ORACLES: CommandLineRunnerTest::testIssue504 supplies integration-level expected compiler behavior for issue 504.  
CASES: Expressions whose truthiness is statically known versus values requiring unknown due to coercion/side effects.  
CASES: Normal literals and boundary numeric/string cases relevant to JavaScript boolean conversion.  
CASES: Error/regression case yielding false in buggy code but unknown per existing test assertion.  
RISKS: NodeUtil methods are package-private; tests likely need com.google.javascript.jscomp package access.  
RISKS: Provided context omits method bodies and exact AST construction; derive inputs only from existing project APIs/tests.