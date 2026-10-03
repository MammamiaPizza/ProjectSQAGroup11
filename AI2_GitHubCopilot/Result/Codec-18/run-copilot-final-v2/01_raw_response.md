TARGETS: equals(CharSequence,CharSequence) — equality comparison, especially when lengths differ.
ORACLES: Return true if identical characters with same length; false otherwise. Must never throw
StringIndexOutOfBoundsException.
CASES: normal: identical strings, same-length different strings. boundary: empty vs empty, empty vs
"a", single-char matches. error: cs1 longer, cs2 longer, mixed CharSequence types (String,
StringBuilder).
RISKS: Buggy loop bound likely uses max length or only one argument’s length; testEqualsCS1/CS2
trigger IndexOutOfBounds on different-length inputs. No fixed code reference.