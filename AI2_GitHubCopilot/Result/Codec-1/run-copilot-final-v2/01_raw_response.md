TARGETS: SoundexUtils.clean(String) – internal case conversion, likely without Locale.ENGLISH
TARGETS: Caverphone.caverphone/encode, Metaphone.metaphone/encode – delegate to clean
ORACLES: Expected output is invariant under default locale change; match Locale.ENGLISH result
CASES: Inputs with 'i'/'I' (e.g., "indigo") — verify encode output same for tr and en
CASES: Inputs with Turkish-specific 'İ'/'ı' — may be rejected or produce consistent result
CASES: Replicate triggers: Metaphone("i") -> "I" not "İ"; Caverphone -> "A..." not "..."
RISKS: SoundexUtils is package-private, clean is static; cannot mock; must set Locale globally in
test