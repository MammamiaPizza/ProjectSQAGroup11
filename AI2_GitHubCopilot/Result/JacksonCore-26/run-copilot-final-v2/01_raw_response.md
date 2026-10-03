TARGETS: NonBlockingJsonParser.nextToken(), _finishToken(), _finishTokenWithEOF()
TARGETS: _skipWS(int), _startFieldName(int), _startFieldNameAfterComma(int), _startValue(int)
TARGETS: Location tracking via _inputPtr, _currInputRow, _currInputRowStart, token offsets
ORACLES: AsyncLocationTest::testLocationOffsets expects correct column (1-based) in token locations
ORACLES: JsonLocation.getColumnNr() returns column offset; expected=1, actual=3 indicates row-reset
bug
CASES: Empty nonblocking feed then endOfInput; token stream with field names at column 1
CASES: JSON with nested objects; comma after value resets column tracking for next field name
CASES: boundary: single-byte vs multi-byte UTF-8 in field names affecting column count
RISKS: Non-visible feeds (needMoreInput=true) may skip column updates during buffer refills
RISKS: Limited context; exact line affected unclear—likely _skipWS or field-name start resets column
late