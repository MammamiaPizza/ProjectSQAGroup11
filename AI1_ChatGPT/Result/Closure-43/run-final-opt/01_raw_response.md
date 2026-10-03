TARGETS: TypedScopeCreator scope/type creation affecting @lends object-literal properties and inferred function returns.
ORACLES: Existing TypeCheckTest::testLends10/11 expected diagnostic text includes "inconsistent return type".
CASES: @lends object literal with methods/values whose returns conflict with the declared/inferred target return type.
CASES: Compare valid consistent return types versus conflicting returns under @lends annotation processing.
CASES: Boundary: nested function/object-literal properties should not be attributed to the wrong lends target.
RISKS: TypedScopeCreator APIs are mostly private; test through compiler/type-check diagnostics rather than direct calls.
RISKS: Context omits full test source and exact JavaScript fixtures; derive inputs only from existing test conventions.