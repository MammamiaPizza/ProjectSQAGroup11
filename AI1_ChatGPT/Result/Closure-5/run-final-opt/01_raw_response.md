TARGETS: InlineObjectLiterals.process; inlining eligibility for object-literal variables and property references.
ORACLES: Existing trigger InlineObjectLiteralsTest::testNoInlineDeletedProperties is the expected-result source.
CASES: Object literal whose property is deleted; verify it is not inlined/rewritten unsafely.
CASES: Normal inlinable object literal with stable property references; preserve generated-code semantics.
CASES: Boundary mixes property reads/writes with delete operations before or after use.
RISKS: Deletion can make initial property values stale; staleVars/blacklisting must prevent inlining.
RISKS: Private eligibility/reference analysis behavior is inferred only from signatures; implementation context is absent.