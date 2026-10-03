TARGETS: TarUtils.parseOctal(byte[], int, int), notably space and NUL handling.  
ORACLES: Existing TarUtilsTest::testParseOctal and COMPRESS-278 trigger behavior.  
CASES: Trigger field: byte[]{' ', 0}, offset 0, length 2.  
CASES: Normal octal digits, leading/trailing NULs, and field-offset parsing.  
CASES: Boundary short fields containing only padding or a single octal digit.  
RISKS: Full existing test assertions and parseOctal implementation context are unavailable.