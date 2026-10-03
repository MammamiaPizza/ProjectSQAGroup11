TARGETS: HtmlTreeBuilderState.InBody generic start/end-tag process; anythingElse dispatch.
TARGETS: end-tag matching via inScope/currentElement().nodeName and popStackToClose.
TARGETS: case preservation for unknown tags (X, y, r) so </X> closes <X>.
ORACLES: HtmlParserTest::caseSensitiveParseTree expected tree <r> <X> A </X> <y> B </y> </r>.
ORACLES: actual <r> <X> A <y> B </y> </X> </r> means </X> was ignored; use assertion text.
CASES: <r><X>A</X><y>B</y></r> must produce X closed before y, siblings under r.
CASES: mismatch forms like </x> vs <X> under preserved case should be checked.
CASES: EOF auto-close after </r> must unwind correctly if </X> was not matched.
RISKS: only supplied signatures/failure text available; cannot inspect full process or settings
behavior.