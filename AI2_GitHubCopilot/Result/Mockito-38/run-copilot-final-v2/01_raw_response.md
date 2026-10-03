TARGETS: getSuspiciouslyNotMatchingArgsIndexes, safelyMatches, toStringEquals
ORACLES: Expected: no NPE when arg is null; null arg with null-equivalent Matcher matches; null with
non-null Matcher is mismatch
ORACLES: Verify returned Integer[] contains correct indices of mismatched args; verify no exception
thrown
CASES: Single null arg, null Matcher (should match)
CASES: Single null arg, expectsNonNull Matcher (should be suspicious index)
CASES: Multiple args mixture: null, non-null, matchers mixture; verify correct indices
CASES: Empty matchers, empty arguments, or both empty (boundary)
CASES: Arguments array contains null at various positions
RISKS: safelyMatches/toStringEquals likely call arg.toString() triggering NPE for null; test must
cover all null-entry paths
RISKS: Unknown internal behavior: need to rely solely on given signatures; ensure no other hidden
dependencies cause NPE