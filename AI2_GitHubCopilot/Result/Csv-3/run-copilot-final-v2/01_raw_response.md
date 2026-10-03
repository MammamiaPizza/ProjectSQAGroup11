TARGETS: Lexer escape handling in nextToken/read logic; isEscape when backslash is escape char;
quoting with backslash escaping.
ORACLES: expected records from CSVParserTest.testBackslashEscaping; MySQL null \N mapping from
CSVLexerTest.testEscapedMySqlNullValue; literal unescape from testEscapedCharacter.
CASES: " inside quoted field; \; \n,\r,\t escapes; \N null; consecutive escapes; empty field;
unclosed quote after escape; delimiter after backslash.
RISKS: Lexer abstract; concrete CSVLexer not visible. Inference limited to test failure diffs and
Lexer API signatures.