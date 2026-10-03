TARGETS: InlineVariables.process and InliningBehavior scope inlining paths  
TARGETS: declaration/initialization/reference validation; alias-candidate handling  
ORACLES: IntegrationTest::testSingletonGetter1 assertion is the only stated expected-result source  
CASES: singleton-getter compilation path exercising variable inlining and alias tracking  
CASES: valid declaration with initialization and subsequent references  
CASES: invalid declaration, initialization, or reference rejected from inlining  
CASES: l-value references and forbidden variables remain non-inlined  
RISKS: exact transformed JavaScript/output expectation is not provided  
RISKS: constructor/configuration API and Mode visibility are not supplied