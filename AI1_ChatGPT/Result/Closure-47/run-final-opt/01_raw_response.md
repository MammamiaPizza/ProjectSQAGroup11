TARGETS: SourceMapConsumerV3.parse and getMappingForLine mapping decode/count behavior.
TARGETS: SourceMap source-location fixup, prefix mappings, wrapper/starting-position output behavior.
ORACLES: Existing SourceMapGeneratorV3Test golden JSON and mapping-count assertions.
CASES: Parse source meta maps; verify original-source count and generated-to-original lookup.
CASES: Multiline, multi-function, literal, and merged mappings with line/column boundaries.
CASES: SourceMap append output with prefix mappings, wrapper prefix, and starting position.
RISKS: Many failures indicate an omitted/shifted mapping, affecting counts and serialized mappings.
RISKS: Context lacks implementation/diff and exact expected strings; rely only on stated trigger oracles.