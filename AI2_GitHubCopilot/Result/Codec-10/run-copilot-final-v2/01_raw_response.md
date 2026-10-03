TARGETS: Caverphone.caverphone(String) final "mb" processing order; encode(String); encode(Object);
isCaverphoneEqual
ORACLES: CODEC-117 trigger value for "mbmb" = "MPM1111111" (was "MMP1111111")
CASES: "mbmb" normal; strings ending in "mb"; upper/lower case equivalence via isCaverphoneEqual
CASES: boundary empty string/null input behavior; encode(Object) non-String type -> EncoderException
RISKS: only one provided expected output ("mbmb"); no oracle for other "mb"-ending words
RISKS: avoid inventing encodings; no API/spec beyond given signatures/trigger