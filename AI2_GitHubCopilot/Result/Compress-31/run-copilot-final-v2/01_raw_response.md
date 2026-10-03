TARGETS: parseOctal(byte[],int,int) NUL-byte validation
ORACLES: IllegalArgumentException for any byte==0 in range; TarUtilsTest.testParseOctalInvalid
expects it
ORACLES: testCOMPRESS178 expects IOException when reading an entry with invalid octal (likely
triggered via parseOctal)
CASES: valid octal string; zero-length; offset at buffer bounds; embedded NUL at offset, mid, end of
parsed range
CASES: negative flag (0xff) with trailing NUL; all NUL bytes; mixed digits+NUL; NUL after valid
octal prefix
RISKS: exact exception for non-octal digits (e.g., 8,9) unknown; may be IOException or
IllegalArgumentException, only NUL confirmed
RISKS: testCOMPRESS178 ties to archive-level IOException; not directly testable from TarUtilsTest
alone; may need TarTestCase setup