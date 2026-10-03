TARGETS: PeepholeFoldConstants array/get-element folding, especially tryFoldGetElem and tryFoldArrayAccess.
ORACLES: Existing testIssue522: optimized testcode must produce no JSC_INDEX_OUT_OF_BOUNDS_ERROR.
CASES: Array literal access at valid index 1 (reported NUMBER 1.0 / array size 1 boundary).
CASES: In-range, first, last, and out-of-range numeric indices; ensure only valid accesses fold safely.
RISKS: Private methods require compiler-level test harness; supplied context omits exact input/output assertion source.
