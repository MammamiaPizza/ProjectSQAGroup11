TARGETS: TypedScopeCreator.createScope, defineObjectLiteral, GlobalScopeBuilder.visit (for @lends
handling), and related scope-resolution methods
ORACLES: Expected "inconsistent return type" JSError when @lends prototype has a method whose return
type conflicts with the target class's interface/declaration
CASES: Normal: @lends with perfectly matching return types. Error: mismatched returns (testLends10,
testLends11). Boundary: nested @lends, multiple @lends on same literal
RISKS: Exact fix location uncertain; tests must replicate the precise typo/type mismatch that
triggers the missing error in buggy version