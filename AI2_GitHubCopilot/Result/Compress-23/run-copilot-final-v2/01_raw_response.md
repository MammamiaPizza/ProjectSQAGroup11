TARGETS: Coders.LZMADecoder.decode – use coder dictionary-size option to construct LZMAInputStream.
ORACLES: No UnsupportedOptionsException for sizes within XZ max; overflow sizes should be clamped to
max, decoding succeeds; data integrity can be verified via round-trip.
CASES: Default dict (e.g., 2^20), max-supported dict, oversized dict (1 GiB), missing property,
zero/negative sizes.
RISKS: XZ max-dict bound may drift with library versions; tests need pre-built 7z archives with
specific header dict sizes; mock may be needed to avoid file dependency.