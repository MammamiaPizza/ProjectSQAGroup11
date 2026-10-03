TARGETS: Coders.LZMADecoder decode behavior for LZMA-coded 7z headers with non-default dictionary sizes.
ORACLES: SevenZFileTest::testCompressedHeaderWithNonDefaultDictionarySize must complete without UnsupportedOptionsException.
CASES: Read a 7z archive whose compressed header uses a non-default LZMA dictionary size.
CASES: Verify header/archive access succeeds and expected entries remain readable after LZMA header decoding.
RISKS: Coders is package-private; direct tests may require the sevenz package or exercise it via SevenZFile.
RISKS: Context provides no archive fixture format or expected decoded contents beyond the named trigger and exception.