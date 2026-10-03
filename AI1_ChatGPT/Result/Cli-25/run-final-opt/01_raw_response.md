TARGETS: HelpFormatter.printWrapped(pw,width,nextLineTabStop,text) long-line chunking and continuation indentation.
TARGETS: HelpFormatter.findWrapPos(text,width,startPos) wrap position selection across successive chunks.
ORACLES: Trigger BugCLI162Test::testLongLineChunkingIndentIgnored comparison output is the expected source.
CASES: Long unbroken argument/text exceeding width with nonzero nextLineTabStop; verify chunk boundaries and indent.
CASES: Text wrapping at whitespace near width, and continuation after an initial wrapped chunk.
CASES: Boundary text exactly width, width+1, and chunk sizes affected by indentation/tab stop.
RISKS: Only signatures and failure summary are available; exact expected formatting/newline must come from trigger behavior.