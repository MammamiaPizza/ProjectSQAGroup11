TARGETS: AbstractCommandLineRunner flag initialization, especially --define parsing/validation.
TARGETS: RemoveConstantExpressions.process and callback simplification of CALL and NEW expressions.
ORACLES: Existing triggers: testDefineFlag3, RemoveConstantExpressionsTest.testCall1/testNew1.
ORACLES: Compiler output/AST assertions in existing tests are the expected-result source.
CASES: --define value FOO="x'" must follow accepted flag syntax rather than throw FlagUsageException.
CASES: CALL/NEW constant-expression removal must preserve required side-effect nodes.
CASES: Normal removable constants; mixed constant/side-effect arguments; empty/boundary argument lists.
RISKS: Context omits implementations and exact transformed AST/output expectations.
