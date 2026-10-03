TARGETS: PeepholeReplaceKnownMethods string/array join folding, especially join elements containing addition.
ORACLES: Existing triggers testStringJoinAdd and testNoStringJoin provide expected optimized/non-optimized output.
CASES: Fold join with string-compatible additions; verify resulting expression/value preserves join semantics.
CASES: Non-string join receivers/arguments must not be folded (testNoStringJoin behavior).
RISKS: Target fold methods are private; test through compiler peephole optimization harness.
RISKS: Context lacks source/test assertions and exact expected transformed JavaScript.