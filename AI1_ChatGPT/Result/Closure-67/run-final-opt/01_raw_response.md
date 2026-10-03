TARGETS: AnalyzePrototypeProperties.process; alias/prototype-property reference collection and propagation.  
TARGETS: getAllNameInfo output, NameInfo declarations/references reachable after process.  
ORACLES: Existing RemoveUnusedPrototypePropertiesTest.testAliasing7 assertion failure.  
CASES: Alias chains involving prototype properties; verify referenced properties are retained/marked used.  
CASES: Direct prototype assignment versus aliased prototype/property access.  
CASES: Global function declarations and assignments participating in aliasing.  
RISKS: Private analysis internals require compiler AST/pass-level test setup.  
RISKS: No exact expected output/API behavior is supplied beyond the triggering test.