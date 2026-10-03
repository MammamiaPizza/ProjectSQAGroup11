TARGETS: NumberUtils.createNumber(String), especially integral literals with trailing l/L.  
ORACLES: LANG-300 trigger: createNumber("1l") must not throw NumberFormatException.  
CASES: "1l", "1L", signed forms, and long boundary values with l/L; assert Long value/type where supported.  
CASES: Unsuffixed integers and decimal/exponent inputs to guard existing createNumber classification.  
RISKS: Preserve invalid-number rejection; only trigger behavior is specified, not all suffix edge semantics.  
RISKS: Context exposes no source/test body; derive expectations only from API and LANG-300 failure.