TARGETS: optimizeSubtree/apply routes to tryFoldStandardConstructors, tryFoldLiteralConstructor,
tryFoldRegularExpressionConstructor.
TARGETS: areValidRegexpFlags, areSafeFlagsToFold, makeForwardSlashBracketSafe,
containsUnicodeEscape.
ORACLES: expected transformation asserted inside PeepholeSubstituteAlternateSyntaxTest#testIssue291.
CASES: RegExp folding for safe flags (empty, g, m, i and combos) vs invalid/unsafe flag strings.
CASES: regexp source with forward slash/brackets requiring makeForwardSlashBracketSafe escaping.
CASES: unicode-escape sources where containsUnicodeEscape must block or permit folding.
CASES: non-literal RegExp constructor arguments that must not fold; literal String/Array
constructors.
RISKS: private methods need driving through optimizeSubtree, then inspect resulting AST/string.
RISKS: exact expected output for issue291 is not given here; avoid asserting invented strings.
RISKS: buggy-version Closure-87b only; no other Closure version may serve as oracle.