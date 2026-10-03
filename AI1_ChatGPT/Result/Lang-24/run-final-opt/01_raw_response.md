TARGETS: NumberUtils.isNumber(String), the sole triggered and modified behavior.  
ORACLES: Existing NumberUtilsTest::testIsNumber assertions are the expected-result source.  
CASES: Valid/invalid signed numeric strings across integer, decimal, exponent, and type-suffix paths.  
CASES: Boundary syntax: empty/null, sign-only, decimal point placement, exponent sign/digits, hex prefix/digits.  
RISKS: Exact failing literal and intended LANG-664 rule are not provided; avoid assuming unshown expectations.