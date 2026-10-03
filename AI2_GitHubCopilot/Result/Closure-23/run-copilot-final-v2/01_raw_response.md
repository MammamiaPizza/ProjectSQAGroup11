TARGETS: tryFoldGetElem, tryFoldArrayAccess, and any related index validation logic.
ORACLES: correct folding must not throw JSC_INDEX_OUT_OF_BOUNDS_ERROR; for out-of-bounds constant
indices, either leave subtree unfoled or fold to undefined, matching JS semantics.
CASES: in-bounds literal index on constant array; index 0, index equals length-1 (boundary); index =
length (out-of-bounds); negative index; non-integer index; non-array left-hand side; empty array;
non-constant index.
RISKS: only buggy version available; exact expected behaviour (emit error vs. leave unfoled vs. fold
to undefined) may be ambiguous; fix may involve silencing the error rather than a fold change.