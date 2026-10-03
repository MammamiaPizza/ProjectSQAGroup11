TARGETS: VarCheck.process must call compiler.reportCodeChange when extern modification occurs.
TARGETS: Normalize.process must not throw RuntimeException on externs modified by VarCheck.
ORACLES: Spy on a mock Compiler.reportCodeChange to verify call count and description.
ORACLES: Expect no RuntimeException from Normalize.process after VarCheck synthesized externs.
CASES: Externs referencing an undeclared property triggers one reportCodeChange call.
CASES: Multiple extern property references trigger exactly one reportCodeChange each.
CASES: Empty or untouched externs produce zero reportCodeChange calls.
RISKS: Private reportCodeChange in Normalize cannot be tested directly via unit test.
RISKS: Internal Compiler state may be hard to reset; use fresh instances per test case.
RISKS: AST shape of synthesized externs may vary; test with known externs inputs only.