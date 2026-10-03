TARGETS: XmlTokenStream.next() (mixed-content case), _handleRepeatElement(), repeatStartElement(),
_collectUntilTag(), _skipUntilTag()
ORACLES: XmlTextTest::testMixedContent expects text value "27" after mixed-content parse; expected
count from assertion
CASES: Mixed content (text, child elements, more text); repeat-element buffering; normal single
text, empty text, whitespace-only
RISKS: _handleRepeatElement or repeatStartElement may reset/miss _textValue; buffering logic may
drop trailing text after child element