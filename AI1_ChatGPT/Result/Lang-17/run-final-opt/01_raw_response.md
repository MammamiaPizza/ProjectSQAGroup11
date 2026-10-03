TARGETS: CharSequenceTranslator.translate(CharSequence) and translate(CharSequence, Writer) Unicode code-point iteration.
TARGETS: translate(CharSequence, Writer) must advance by consumed code points, not UTF-16 char count.
ORACLES: LANG-720 trigger expects supplementary character U+20BB7 preserved before trailing "A".
ORACLES: Abstract translate(input,index,out) consumed-count contract determines output/index advancement.
CASES: Input supplementary Unicode character followed by BMP "A"; identity/no-match translator behavior.
CASES: BMP-only input, empty input, and supplementary character at end.
CASES: Translator consuming one code point at a supplementary-character index.
RISKS: Need a concrete test translator; abstract method behavior must be defined locally.
RISKS: Context lacks exact existing test helpers and intended behavior for null inputs/writer failures.