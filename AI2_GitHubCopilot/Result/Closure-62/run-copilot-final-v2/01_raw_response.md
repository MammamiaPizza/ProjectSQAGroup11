TARGETS: formatError(JSError),formatWarning(JSError),LineNumberingFormatter.formatLine(String,int)
ORACLES: Expected output from trigger tests (assert string contains trailing spaces exactly as in
source, no trimming)
CASES: Source line ending with space; line ending with multiple spaces; line with only spaces; line
with no trailing space; empty source; null error; error region with trailing-space line
CASES: Multi-line source each ending with different trailing-space counts; line with tab+space at
end
RISKS: Cannot inspect actual test code; must infer exact expected formatting from test names
TARGETS: LightweightMessageFormatter(SourceExcerptProvider) constructor determines source-excerpt
retrieval; formatRegion may also contribute
RISKS: Private format(JSError,boolean) untestable directly; must cover via
formatError/formatWarning; formatRegion interaction unclear