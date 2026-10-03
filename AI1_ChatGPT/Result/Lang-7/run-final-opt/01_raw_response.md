TARGETS: NumberUtils.createNumber(String), especially exponent parsing and NumberFormatException behavior.  
ORACLES: Existing trigger NumberUtilsTest::testCreateNumber expects NumberFormatException.  
CASES: Invalid numeric strings with malformed exponent/sign combinations should throw NumberFormatException.  
CASES: Contrast valid integer, decimal, exponent, and type-suffixed inputs accepted by createNumber.  
RISKS: Exact malformed input and intended type-selection rules are not supplied in this context.