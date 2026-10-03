TARGETS: HelpFormatter.printWrapped(PrintWriter,int,int,String) long-line wrapping with indent/tab stop.
TARGETS: HelpFormatter.findWrapPos(String,int,int) behavior when remaining width is exhausted by indent.
ORACLES: Trigger test expects no IllegalStateException for long text when indent leaves no description room.
ORACLES: Printed output via PrintWriter is the observable result; configured width/newline govern wrapping.
CASES: Long unbroken chunk with width <= nextLineTabStop; verify completion and line chunking.
CASES: Long text with whitespace near wrap boundary and nonzero nextLineTabStop.
CASES: Boundary width equal to indent, and width one greater/less than indent.
RISKS: Exact expected formatting/chunk output is not provided; avoid assuming undocumented spacing.