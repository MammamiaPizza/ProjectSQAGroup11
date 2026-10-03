TARGETS: CollapseProperties.process; alias inlining, namespace checks, declaration/reference collapsing.  
ORACLES: Existing CollapsePropertiesTest::testIssue931 assertion; compiler output/diagnostics produced by process.  
CASES: Reproduce issue 931 input through existing test harness; assert transformed source matches its expected output.  
CASES: Alias chains and namespace property references before/after declarations; verify all references are flattened consistently.  
CASES: Simple stub declarations and descendant properties; verify collapse preserves declarations and aliases.  
RISKS: Private helpers require testing via process/compiler harness, not direct invocation.  
RISKS: Context omits issue-931 source and expected output; derive inputs/oracles only from existing test fixtures.