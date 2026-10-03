TARGETS: InlineObjectLiterals.process, InliningBehavior.afterExitScope, isInlinableObject,
fillInitialValues, staleVars tracking
ORACLES: testNoInlineDeletedProperties asserts objects with property deletions are NOT inlined
CASES: normal inlinable objects (positive), objects with top-level property deletion (negative),
delete nested property, delete after init
RISKS: No visibility into reference-map or deletion-detection logic; must infer from bug report that
deleted properties are missed