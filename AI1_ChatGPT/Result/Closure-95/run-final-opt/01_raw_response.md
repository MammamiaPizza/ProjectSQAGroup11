TARGETS: TypedScopeCreator scope creation; AbstractScopeBuilder.isQnameRootedInGlobalScope(Node)
TARGETS: GlobalScopeBuilder/LocalScopeBuilder qualified-name declaration and type inference
ORACLES: Existing triggers: expected warning in TypeCheckTest.testQualifiedNameInference5
ORACLES: Existing trigger: TypedScopeCreatorTest.testGlobalQualifiedNameInLocalScope assertions
CASES: Qualified name rooted globally but referenced/declared from a local function scope
CASES: Qualified-name inference path that must emit the expected TypeCheck warning
RISKS: Available signatures expose private implementation; tests should use existing compiler/test harness
RISKS: No source diff or assertion details supplied; expected warning text/type is unspecified