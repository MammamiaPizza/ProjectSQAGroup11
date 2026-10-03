TARGETS: Options.getMatchingOptions(String opt) – prefix-based matching and exact-match resolution
TARGETS: Options.hasLongOption(String opt) / hasShortOption(String opt) – exact existence checks
ORACLES: For an exact long-opt name, getMatchingOptions must return only that option (no ambiguous
prefix matches)
ORACLES: Expected behavior: no AmbiguousOptionException thrown when an exact match exists among
prefix-sharing options
CASES: EQ: add "prefix","prefixplusplus" long opts → getMatchingOptions("prefix") returns ["prefix"]
(exact wins)
CASES: BOUNDARY: add "pref","prefix"; getMatchingOptions("prefix") returns ["prefix"] (exact over
shorter prefix)
CASES: BOUNDARY: only "prefixplusplus" present; getMatchingOptions("prefix") returns
["prefixplusplus"] (sole prefix)
CASES: ERROR: null or empty argument to getMatchingOptions → expected NPE/IAE handling
RISKS: Cannot validate parser-level AmbiguousOptionException; only Options matching logic is
testable
RISKS: Limited to public API; internal option-group interactions may interfere with matching on edge
cases