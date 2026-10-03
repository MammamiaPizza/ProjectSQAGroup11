TARGETS: NameAnalyzer.process; reference calculation and AST removal/replacement paths  
TARGETS: FindReferences traversal; hidden aliases, dependency scopes, and prototype/class reference nodes  
ORACLES: NameAnalyzerTest::testIssue284 assertion; IntegrationTest::testIssue284 must avoid compiler error  
CASES: Reproduce Issue 284 input through NameAnalyzer and verify the existing expected assertion  
CASES: Exercise references involving aliases, parent names, prototype names, and class-defining calls  
CASES: Cover removal/replacement where RHS subexpressions or short-circuit values are consumed  
RISKS: NameAnalyzer is package-private; tests likely require same-package compiler test infrastructure  
RISKS: Issue 284 source input and exact expected transformed output are not provided