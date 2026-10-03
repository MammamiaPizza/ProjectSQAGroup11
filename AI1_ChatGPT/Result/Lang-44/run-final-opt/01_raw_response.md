TARGETS: NumberUtils.createNumber(String), especially inputs reaching suffix/exponent parsing.  
ORACLES: Trigger testLang457; bug report expects no StringIndexOutOfBoundsException for valid edge input.  
CASES: Empty-length numeric prefix with terminal type suffix; verify returned Number type/value or NumberFormatException.  
CASES: Normal integers, decimals, exponents, and L/F/D suffixes around suffix parsing boundaries.  
RISKS: Exact LANG-457 input/expected value is not provided; avoid assuming undocumented acceptance rules.