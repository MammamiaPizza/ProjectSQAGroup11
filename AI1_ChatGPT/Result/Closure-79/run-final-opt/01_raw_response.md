TARGETS: Normalize.process: normalize declarations/labels/constants without INTERNAL COMPILER ERROR.  
TARGETS: VarCheck.process/visit: detect undeclared references in externs and synthesize extern declarations.  
ORACLES: Trigger assertions require compiler.reportCodeChange() for extern property, var, and call references.  
ORACLES: NormalizeTest::testIssue is the source for no compiler-internal-error behavior.  
CASES: Externs containing property references, including the two trigger property-reference scenarios.  
CASES: Externs containing an undeclared variable reference and an undeclared call expression.  
CASES: Verify synthesized extern declaration insertion and code-change reporting for each discovered name.  
RISKS: APIs are mostly private/package-private; tests likely need existing compiler test harnesses.  
RISKS: Exact transformed AST/source and diagnostic expectations are not provided.