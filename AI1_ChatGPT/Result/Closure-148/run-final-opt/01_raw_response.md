TARGETS: PeepholeFoldConstants typeof folding, especially operand-type handling in tryFoldTypeof.
TARGETS: SourceMap.appendTo output and LineMapper mapping/line-column adjustment behavior.
ORACLES: Existing PeepholeFoldConstantsTest.testFoldTypeof assertions define expected folded JS.
ORACLES: SourceMapTest.testGoldenOutput0..4 golden strings define exact serialized source-map output.
CASES: typeof literals, names, and expressions where folding must preserve JavaScript typeof semantics.
CASES: Source maps with mappings across lines, prefixes/offsets, overlaps, and escaped source/name text.
RISKS: SourceMap output comparisons are formatting-sensitive, including spacing, JSON escaping, and final layout.
RISKS: Available methods are private; exercise through existing public/compiler test pathways only.
RISKS: Context omits constructors and mapping-add APIs, limiting direct isolated SourceMap test design.