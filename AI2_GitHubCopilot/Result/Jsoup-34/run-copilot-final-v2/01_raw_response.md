TARGETS: CharacterReader.consumeToAny boundary checks; internal nextIndexOf index calculation.
ORACLES: No ArrayIndexOutOfBoundsException; correct substring or empty string on unmatched.
CASES: Empty input; input with no matching char; match exactly at last character.
CASES: Search starting at last char; search past EOF; single-char input unmatched.
RISKS: Private nextIndexOf not directly testable; unknown internal buffer/pos state for
consumeToAny.