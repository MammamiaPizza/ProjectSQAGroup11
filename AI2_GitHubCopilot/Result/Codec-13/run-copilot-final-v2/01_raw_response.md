TARGETS DoubleMetaphone.isDoubleMetaphoneEqual(String,String) lacks null guard -> NPE
TARGETS DoubleMetaphone.isDoubleMetaphoneEqual(String,String,boolean) same risk
TARGETS DoubleMetaphone.doubleMetaphone(String) may NPE if value is null
TARGETS StringUtils.getBytes*(String) called with null string -> NPE
ORACLES isDoubleMetaphoneEqual(null,) should return false; doubleMetaphone(null) -> null
ORACLES No NPE should occur; behavior inferred from typical StringEncoder contract
CASES null vs null, null vs "abc", "abc" vs null, empty strings, single-chars, maxCodeLen exceeding
CASES Strings with "cz", "wicz", "c", "x" (slavoGermanic triggers), special chars, diacritics
RISKS Only NPE known; other methods (encode, handle) may also lack null checks; CharSequenceUtils
unrevealed