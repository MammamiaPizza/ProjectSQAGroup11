TARGETS: ReaderBasedJsonParser.getTokenLocation(), getCurrentLocation(), _updateLocation,
_updateNameLocation; UTF8StreamJsonParser same
ORACLES: Assert.assertEquals expected char offset after each nextToken(); use
JsonLocation.getCharOffset() from known JSON strings
CASES: Object with single field, multiple fields, string/int values, leading/trailing whitespace;
check offset after field name, colon, value, comma, closing brace
RISKS: Off-by-one in offset after field name due to _updateNameLocation not resetting
_tokenInputRow/_tokenInputCol; char vs byte offset mismatch in UTF8StreamJsonParser