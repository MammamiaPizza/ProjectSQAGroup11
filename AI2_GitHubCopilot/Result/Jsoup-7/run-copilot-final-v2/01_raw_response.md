TARGETS: Document.normalise(), head(), body(), outerHtml()
ORACLES: testNormalisesStructure expected output; HTML5 spec implied head/body closure rules
CASES: malformed: script/noscript in head, body content before body tag, missing head, nested body
RISKS: normalisation algorithm specifics unclear; interaction with parser not testable from unit;
charset not tested