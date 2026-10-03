TARGETS: StringUtils.replace(String, String[], String[]) array-based replacement behavior.
ORACLES: Existing trigger test and LANG-552 summary identify NPE avoidance as required.
CASES: Normal parallel search/replacement arrays producing combined replacements.
CASES: Null input, null arrays, empty arrays, and null search/replacement elements.
CASES: Unequal array lengths and repeated/overlapping search strings.
RISKS: Full replace overload signatures/contract are truncated; derive expectations only from available tests/source.