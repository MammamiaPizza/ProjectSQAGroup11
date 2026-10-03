TARGETS: ReaderBasedJsonParser parsing of unquoted field names with non-ASCII characters (issue 510).
ORACLES: Existing trigger NonStandardUnquotedNamesTest::testUnquotedIssue510; token/name results and no AIOOBE.
CASES: Enable ALLOW_UNQUOTED_FIELD_NAMES; parse object with an unquoted name containing character code 256.
CASES: Verify START_OBJECT, FIELD_NAME, expected field name/value token, and END_OBJECT sequence.
CASES: Boundary names around Latin-1 table limit: code 255 versus code 256, if supported by trigger input.
RISKS: Parser construction/factory configuration APIs and exact expected input are not included in provided context.
RISKS: Do not directly test protected/private parser helpers; exercise via public parsing API.