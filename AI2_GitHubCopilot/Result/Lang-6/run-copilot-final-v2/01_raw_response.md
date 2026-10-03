TARGETS: CharSequenceTranslator.translate(CharSequence,Writer) loops over chars, advancing index by
the int returned from abstract translate.
TARGETS: The convenience translate(String) method delegates to translate(CharSequence,Writer) and
catches IOException.
ORACLES: For each invocation, abstract translate returns the number of characters consumed; for a
supplementary character it must return 2.
ORACLES: Expected result of translating a string with surrogates is known: the hex escaping of the
full code point (e.g., \uD83D\uDE00 → \U0001F600).
CASES: Input with a valid surrogate pair at the very end of the string – previously caused
StringIndexOutOfBoundsException (index exceeds length).
CASES: Input with a surrogate pair at the start, middle, and end in a longer string to verify index
advancement accuracy.
CASES: Unpaired surrogates: lone high surrogate at end, lone low surrogate at start – should not
throw out-of-bounds but may throw IllegalArgumentException or produce escaped form.
RISKS: The bug is in the loop’s index increment logic; test must directly use
translate(CharSequence,Writer) on a subclass that handles surrogates to trigger the exception.
RISKS: Without a concrete translator implementation that correctly returns 2 for surrogates, the
test cannot reproduce the fix; a minimal test double is needed.
RISKS: The abstract translate method’s contract is that it may throw IOException;
translate(CharSequence,Writer) rethrows, translate(String) wraps in RuntimeException.