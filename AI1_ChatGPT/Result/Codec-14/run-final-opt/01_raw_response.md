TARGETS: PhoneticEngine.encode compatibility output; Lang.guessLanguages/guessLanguage; Rule parsing/matching.
ORACLES: Existing regression expected output; language-rule resources; public API results.
CASES: Reproduce regression input producing no trailing "|vntsn" alternative.
CASES: Names exercising Ashkenazi, generic, and Sephardic language-rule selection.
CASES: Normal multiword/apostrophe inputs; concat and max-phoneme behavior if exposed by constructors.
RISKS: Exact trigger input and full expected encoding are absent from supplied context.
RISKS: Resource rule ordering/parsing may affect alternatives and language-set filtering.