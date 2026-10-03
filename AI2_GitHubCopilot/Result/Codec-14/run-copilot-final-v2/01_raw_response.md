TARGETS: PhoneticEngine.encode(input) combines phonemes; PhonemeBuilder.apply(expr,max) joins;
Rule.patternAndContextMatches; Lang.guessLanguages filters.
ORACLES: regression test expected values for known inputs (e.g., "dzn","bntsn","bnzn","vndzn");
language resource file rules define acceptance; Phoneme join uses language restriction.
CASES: normal single-word; multi-word with spaces/apostrophes; empty string; input hitting
maxPhonemes limit; input matching no language rule; boundary maxPhonemes=1.
RISKS: expected output relies on undocumented original-version behavior; resource file semantics may
change; language guessing outcomes depend on rule order.