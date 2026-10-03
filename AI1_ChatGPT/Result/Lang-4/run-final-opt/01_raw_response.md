TARGETS: LookupTranslator.translate(CharSequence input, int index, Writer out): lookup matching and consumed count.
TARGETS: LookupTranslator constructor lookup entries: key lengths influence translate search bounds.
ORACLES: LANG-882 trigger asserts matched input consumes 3 code points, not 0.
CASES: Match a 3-code-point lookup key at index 0; verify returned consumption and writer output.
CASES: Verify normal lookup match, no-match return, and matching at a nonzero index.
CASES: Boundary keys near configured shortest/longest lookup lengths.
RISKS: CharSequence subsequence/map-key equality may prevent lookup despite equal character content.
RISKS: Available context omits full lookup data and intended behavior beyond the failing LANG-882 assertion.