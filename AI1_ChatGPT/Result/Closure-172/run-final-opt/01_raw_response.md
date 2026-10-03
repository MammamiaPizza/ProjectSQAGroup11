TARGETS: TypedScopeCreator scope creation, enum discovery/declaration during traversal  
ORACLES: TypeCheckTest::testIssue1024; absence of its previously unexpected warning  
CASES: Compile the Issue1024 JavaScript scenario through type checking and assert expected diagnostics  
CASES: Enum initializer as object literal; enum initializer that is not an object literal  
RISKS: Most relevant methods are private/internal; exercise through compiler/type-check integration  
RISKS: Context provides no source fixture or exact warning text beyond the trigger failure