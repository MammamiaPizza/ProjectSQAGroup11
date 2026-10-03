TARGETS: Caverphone.caverphone/encode, Metaphone.metaphone/encode, SoundexUtils.clean locale-neutral casing.
ORACLES: Existing locale-independence trigger expectations: Turkish Caverphone "A111111111", Metaphone "I".
ORACLES: Soundex/RefinedSoundex trigger behavior must remain valid through SoundexUtils.clean under Turkish locale.
CASES: Set default locale to tr; encode inputs whose lowercase/uppercase I conversion affects normalization.
CASES: Compare Turkish-locale results with locale-neutral expected outputs and restore default locale afterward.
CASES: Exercise Caverphone and Metaphone String and Object encode paths where applicable.
RISKS: SoundexUtils is package-private; test indirectly via Soundex and RefinedSoundex public encoders.
RISKS: Context lacks exact inputs and full expected mappings for Soundex/RefinedSoundex.