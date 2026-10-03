TARGETS: Entities.escape(String), escape(Writer,String) handling supplementary Unicode code points  
ORACLES: Trigger expects U+1D306 escaped as "&#119650;" (not separate surrogate numeric entities)  
CASES: Escape a String containing the high-Unicode character used by StringEscapeUtilsTest  
CASES: Compare String-returning and Writer overload outputs for the same supplementary character  
CASES: Boundary around surrogate pairs; preserve normal BMP/entity escaping behavior where observable  
RISKS: Entities is package-private; tests may need same-package access or exercise via existing public callers  
RISKS: No other-version context; avoid assuming behavior for malformed/unpaired surrogate input