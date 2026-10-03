TARGETS: Caverphone.caverphone(String), especially terminal "mb" replacement ordering.
TARGETS: encode(String), encode(Object), and isCaverphoneEqual(String,String) delegate/result behavior.
ORACLES: Existing trigger: caverphone("mbmb") must return "MPM1111111".
CASES: Terminal/nonterminal "mb" patterns, including "mb", "mbmb", and repeated suffix occurrences.
CASES: Verify 10-character encoded output padding/truncation where applicable.
RISKS: Only the reported "mbmb" oracle is provided; no broader algorithm specification is available.