TARGETS: ReferenceCollectingCallback#addReference, #isBlockBoundary, #visit — basic-block tracking
around for-loop boundaries.
ORACLES: ReferenceCollection#isWellDefined — existing method checks reachability/assignment
semantics; Behavior/DO_NOTHING (no external boolean flags).
CASES: for-loop with block body; for-in loop with aliased initialization; root/global scope
references; empty loop body.
RISKS: Missing version-specific bug details; only one triggering test signature; no exact diff to
confirm which method changed.