TARGETS: HelpFormatter.findWrapPos(String,int,int)
TARGETS: renderWrappedText/printWrapped/renderOptions paths that consume its index
ORACLES: HelpFormatterTest.testFindWrapPos exact wrap-index assertions (3, not 4)
ORACLES: HelpFormatterTest.testRenderWrappedTextWordCut expected word-cut output, no
StringIndexOutOfBounds
CASES: startPos+width>=text.length() returns -1
CASES: no space within width, long token: return width-boundary index, not next space
CASES: token reaches text.length(): guard charAt(text.length()) in forward scan
CASES: newline/tab at/before width: boundary pos vs pos+1
RISKS: renderWrappedText not in provided API; line.separator affects expected text