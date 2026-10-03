TARGETS: SourceMapConsumerV3.parse(String,SourceMapSupplier) – section merging
TARGETS: SourceMapConsumerV3.getMappingForLine – entry count per line
TARGETS: SourceMapConsumerV3.visitMappings – traversal correctness
TARGETS: SourceMap (jscomp) appendTo/reset – serialization fidelity
ORACLES: Expected mapping count from test assertions (e.g., 5 vs 4, 10 vs 9)
ORACLES: Golden file JSON comparison (ComparisonFailure reveals "t":1 mismatch)
ORACLES: getMappingForLine should return null for unmapped positions
CASES: normal mapping, multiline, multifunction, literal mappings golden outputs
CASES: source meta-map with sections (sourceMapMerging), parse errors
RISKS: Cannot inspect golden files or actual expected JSON; diff truncated