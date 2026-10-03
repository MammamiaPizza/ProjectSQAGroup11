TARGETS: addName overloads, rehash(), _verifyNeedForRehash(), _findOffsetForAdd(), makeChild(),
_calcTertiaryShift()
ORACLES: No AIOOBE; size() matches insert count; findName returns all added names; count breakdowns
consistent
CASES: Insert many unique 1-4-quad names to trigger multiple rehashes; include long names causing
spillover past _spilloverStart
CASES: Boundary: add exact hashSize+1 names after root creation; verify no index 512 AIOOBE
CASES: makeChild then add many names in child while parent exists to test shared-table expansion
RISKS: Internal hash area sizing unknown; must cover expansions that reach index 512 (likely during
tertiary/spillover)
RISKS: Parent-child table sharing may cause miscalculated secondary/tertiary offsets; test both
createRoot and makeChild paths