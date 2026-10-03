TARGETS: BitInputStream.readBits(int count) in little/big-endian modes
ORACLES: littleEndianWithOverflow expects 1186; bigEndianWithOverflow expects 8274274654740644818
ORACLES: Values decoded must match specification (COMPRESS-363, byte-order respecting bit packing)
CASES: readBits with 0, 1, 7, 8, 9, 15, 16, 17, 31, 32, 33, 63, 64 bits; max long value
CASES: readBits across byte boundaries, end-of-stream handling, after clearBitCache
CASES: littleEndianWithOverflow: reading >8 bits from a two-byte stream that triggers the overflow
bug
CASES: bigEndianWithOverflow: reading >8 bits from an eight-byte stream, high-bit overflow
RISKS: Only two known failing overflow scenarios; other overflow widths may also be affected
RISKS: Inferred expected values come solely from test expectations; no external spec or fixed
version
RISKS: Edge cases like reading after stream close, negative count, or partial reads untested