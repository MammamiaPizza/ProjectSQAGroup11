TARGETS: SevenZOutputFile.writeFileEmptyStreams, writeFileEmptyFiles, writeHeader, finish
ORACLES: Roundtrip via SevenZFile: no IOException, entry count+names+dirs match, streams readable
CASES: 0 empty (all non-empty), 1 empty, many empty (6,7,8,9), mixed empty/non-empty, zero-size body
RISKS: No 7z spec; reader may share bug; correct property order & condition for empty-file flag
unknown