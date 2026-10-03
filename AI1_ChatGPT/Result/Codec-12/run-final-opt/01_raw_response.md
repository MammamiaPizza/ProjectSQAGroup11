TARGETS: BaseNCodecInputStream read(), bulk read(byte[],offset,len), available(), and skip() behavior.
ORACLES: Triggered Base32InputStreamTest and Base64InputStreamTest assertions define expected results.
CASES: Skip to end then read() returns -1; skip past end returns only remaining decoded bytes.
CASES: Large skip returns remaining decoded-byte count, not an internal buffer-size value.
CASES: skip(negative) throws IllegalArgumentException.
CASES: available() reports expected decoded availability after construction/reads.
CASES: Read after skip preserves correct subsequent decoded text ("ello World" in CODEC-130 trigger).
RISKS: Both Base32 and Base64 wrappers must share consistent decoded-stream skip/availability semantics.
RISKS: Context omits implementation and full test inputs; derive expectations only from listed trigger behavior.