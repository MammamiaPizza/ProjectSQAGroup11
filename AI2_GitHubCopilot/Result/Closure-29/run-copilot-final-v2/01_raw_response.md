TARGETS: InlineObjectLiterals.process, InliningBehavior.afterExitScope, .visit, isInlinableObject,
blacklistVarReferencesInTree, isVarInlineForbidden
ORACLES: Expected JS strings from test resources; assertEquals on compiled output
CASES: testObject10, testObject12, testObject22, testIssue724 unit + IntegrationTest::testIssue724
CASES: Inline object with function, prototype, getter/setter, side effects, this usages; boundary:
no refs, deeply nested objects
RISKS: Exact oracle for inlining correctness unknown; failure outputs not provided; limited to
observed failing tests