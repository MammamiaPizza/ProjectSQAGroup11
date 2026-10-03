TARGETS: ExtendedBufferedReader.readLine() and its line-number advancement logic.
TARGETS: read()/read(char[],int,int) handling of \r and \n terminators.
ORACLES: CSVParserTest.testGetLineNumberWithCR expects line number 1 after a CR-terminated line.
ORACLES: CR, LF, and CRLF should each terminate one line (standard Reader semantics).
CASES: empty/EOF input returns null and leaves line count unchanged.
CASES: lines ending with \r (bug trigger), \n, \r\n, and unterminated at EOF.
CASES: multiple lines, consecutive blank lines, and mixed newline styles.
CASES: boundary long line; lines containing only \r or only \n.
RISKS: getLineNumber() signature is not listed, so exact tracking contract is uncertain.
RISKS: only the trigger expectation is available; buggy implementation details are unverified.