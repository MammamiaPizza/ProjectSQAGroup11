TARGETS: StringUtil.normaliseWhitespace and appendNormalisedWhitespace handling invisible Unicode characters.
ORACLES: Trigger expects "Thisisonelongword" from text containing soft-hyphen/zero-width invisible chars.
CASES: Normal visible text remains unchanged; ordinary whitespace normalization remains covered.
CASES: Invisible characters embedded between words are removed, not retained or converted to spaces.
CASES: Leading invisible characters and mixtures with whitespace under stripLeading true/false.
RISKS: Context does not provide the exact invisible-character set or direct method-level expected outputs.