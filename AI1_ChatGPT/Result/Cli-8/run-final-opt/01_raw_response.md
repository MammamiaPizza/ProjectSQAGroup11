TARGETS: HelpFormatter.printWrapped(PrintWriter,int,String) and protected findWrapPos(String,int,int).
ORACLES: Existing HelpFormatterTest::testPrintWrapped comparison output is the expected-result source.
CASES: Single-line padded text wrapping at spaces; verify line breaks and preserved/trimmed padding.
CASES: Boundary widths around a wrap position; text fitting exactly versus requiring one wrapped line.
CASES: Embedded newline and no-whitespace-long-word behavior, if covered by current method semantics.
RISKS: Exact output depends on configured newline and PrintWriter capture.
RISKS: Context lacks full expected string and implementation body; avoid inferring undocumented formatting.