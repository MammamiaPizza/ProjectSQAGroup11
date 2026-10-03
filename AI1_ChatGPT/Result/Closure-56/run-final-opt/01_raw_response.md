TARGETS: SourceFile.getLine(int), getRegion(int), and line-offset handling for source text without trailing newline.
ORACLES: Existing trigger expects getLine for final unterminated line: "foo2:third line", not null.
ORACLES: JS message extractor syntax-error assertions depend on correct source-line retrieval/location.
CASES: fromCode text with multiple newline-terminated lines plus a final non-newline-terminated line.
CASES: Request final line and its region; verify returned text/location reflects final line.
CASES: Boundary line numbers around first, intermediate, final, and out-of-range lines.
RISKS: getCode may throw IOException for non-preloaded sources; avoid assuming behavior not shown.
RISKS: Context lacks implementation and exact Region semantics; derive only from existing trigger expectations.