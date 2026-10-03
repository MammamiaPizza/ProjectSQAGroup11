TARGETS: addName methods (1–4 quads), findName variants, primaryCount, secondaryCount,
tertiaryCount, totalCount, spilloverCount
TARGETS: _calcOffset, _findSecondary, _findOffsetForAdd, rehash, _verifyNeedForRehash,
_spilloverStart, _appendLongName
TARGETS: makeChild, release, mergeChild – child-table usage and shared state
ORACLES: testCollisionsWithBytesNew187b expects primaryCount=16384; testSyntheticWithBytesNew
expects totalCount=8534
ORACLES: testShortNameCollisionsDirectNew expects primaryCount=1024; testIssue207 expects no
ArrayIndexOutOfBoundsException
ORACLES: expected counts are derived from known-good runs with the same seed; any deviation
indicates counting/sizing bug
CASES: add many distinct names (1‑quad) until table grows; verify totalCount equals number of unique
additions
CASES: add names producing hash collisions to fill secondary area; check secondaryCount>0 and
primaryCount as expected
CASES: border case: add enough entries to overflow primary+secondary into spillover; verify
spilloverCount and no array bounds error
RISKS: miscalculation of tertiary start or shift can cause an index=256 out of bounds when accessing
_hashArea on probe