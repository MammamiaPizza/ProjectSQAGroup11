TARGETS: tryFoldGetElem(Node,Node,Node), tryFoldArrayAccess(Node,Node,Node),
tryFoldArithmeticOp(Node,Node,Node)
ORACLES: Folding must not falsely trigger JSC_INDEX_OUT_OF_BOUNDS_ERROR when array index is within
bounds; result must be correct.
CASES: index=1.0 (float-like int), index=0, index=length-1, index=length (OOB), index=negative,
computed constant index.
RISKS: Limited to bug-report description; contract around error suppression vs. reporting for OOB is
ambiguous.