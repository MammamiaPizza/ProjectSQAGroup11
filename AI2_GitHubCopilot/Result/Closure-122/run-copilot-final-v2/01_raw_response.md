TARGETS: IRFactory.handleBlockComment, handlePossibleFileOverviewJsDoc, setFileOverviewJsDoc
ORACLES: check parsedComments Set contents, verify fileoverview JsDoc flag on target Node
CASES: block comment at script start, block comment not at start, suspicious /** @fileoverview */
spam
RISKS: limited to IRFactory internals; no ParserTest source provided; parsedComments tracking may
affect multiple tests