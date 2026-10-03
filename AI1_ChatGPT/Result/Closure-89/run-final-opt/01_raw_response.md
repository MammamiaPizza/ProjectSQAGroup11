TARGETS: CollapseProperties.process alias inlining and declaration/property collapsing through nested functions.  
TARGETS: GlobalNamespace reference classification for global vs local names and namespace construction.  
ORACLES: Existing CollapsePropertiesTest assertions and compiler output/diagnostics are the expected-result source.  
CASES: Uncollapsible local function receives a property; child property must preserve expected transformation.  
CASES: Alias creation in function depths 1 and 2, including variants covered by depth1_1/_2/_3 triggers.  
CASES: Named constructor and function declarations in local scope with added properties at depths 1 and 2.  
RISKS: Private helpers require exercising via CompilerPass/process rather than direct unit calls.  
RISKS: Context provides no exact input/output assertions; derive expectations only from existing triggered tests.