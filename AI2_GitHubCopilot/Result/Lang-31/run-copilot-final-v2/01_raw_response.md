TARGETS: StringUtils.containsAny(CharSequence,char[]) and containsAny(CharSequence,String) with
supplementary chars
ORACLES: testContainsAnyCharArrayWithSupplementaryChars expects false for non-matching supplementary
search; testContainsAnyStringWithSupplementaryChars expects false similarly
CASES: supplementary char in target only, in search only, in both matching, in both non-matching;
boundary: lone surrogate, empty/null inputs, BMP-only regression
RISKS: methods likely iterate char-by-char not code-point, causing false positives on surrogate
pairs; truncated API list hides containsAny signatures