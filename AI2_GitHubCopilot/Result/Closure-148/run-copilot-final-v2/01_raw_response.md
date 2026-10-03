TARGETS PeepholeFoldConstants.tryFoldTypeof/tryFoldUnaryOperator: fold typeof of known operand
types, leave unknown unmodified.
TARGETS SourceMap.appendTo and LineMapper.addCharEntry/closeLine/writeClosedMapping: adjusted
line/col and VLQ encoding.
ORACLES PeepholeFoldConstantsTest.testFoldTypeof expected folded block(s) vs unchanged AST for
non-foldable typeof.
ORACLES SourceMapTest.testGoldenOutput0..4 compare generated source-map JSON byte-for-byte to golden
outputs.
CASES typeof identifier, string, number, boolean, void/undefined, function; non-foldable unknown
name.
CASES SourceMap: first mapping at origin, consecutive same-line chars, multi-line mappings,
overlapping entries.
CASES SourceMap boundaries: zero-vs-positive col/line deltas and open/close line transitions.
RISKS Golden format details only partially known from failure excerpt; full expected output bodies
unavailable.
RISKS Private methods cannot be called directly; tests must drive public compile/appendTo entry
points.