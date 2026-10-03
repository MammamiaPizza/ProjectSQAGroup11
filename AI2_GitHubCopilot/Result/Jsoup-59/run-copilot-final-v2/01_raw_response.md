TARGETS: Token.Tag.appendTagName, appendAttributeName, reset(StringBuilder), finaliseTag,
newAttribute
ORACLES: Control chars in tag/attr names should not cause empty-string exception;
replacement/stripping is expected
CASES: Ctrl chars (\0, \u001F) in tag name, attr name; after tag name; boundary empty-after-strip;
normal chars unaffected
RISKS: Implementation of control-char filtering unseen; only Token API available, not
HtmlParser/Cleaner wiring