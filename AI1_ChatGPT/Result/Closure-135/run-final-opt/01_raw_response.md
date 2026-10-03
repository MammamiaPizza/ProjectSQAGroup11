TARGETS: DevirtualizePrototypeMethods.process rewrites prototype definitions/calls and preserves receiver typing.
TARGETS: rewriteDefinition/fixFunctionType must retain original instance type for rewritten receiver parameter.
TARGETS: FunctionType subtype/extends behavior implicated by TypeCheckTest::testGoodExtends9 warnings.
ORACLES: Existing trigger expected typed-AST output: foo$self and bar$self names have type a, not null.
ORACLES: Existing TypeCheckTest oracle: valid extends scenario produces no warnings.
CASES: Class a prototype methods with zero, one, and multiple declared arguments; rewritten calls retain return types.
CASES: Constructor/new a followed by rewritten foo/bar/baz calls; receiver argument is typed as a.
CASES: Valid inheritance/extends case exercised through TypeCheckTest testGoodExtends9.
RISKS: Available context omits FunctionType changed methods and source details; assert only trigger-observable behavior.