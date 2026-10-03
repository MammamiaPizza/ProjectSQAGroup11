TARGETS: BitInputStream.readBits(int), cache handling across reads, BIG_ENDIAN and LITTLE_ENDIAN byte orders.
ORACLES: Existing trigger assertions: little-endian overflow returns 1186; big-endian overflow returns 8274274654740644818.
CASES: Read bit counts that cross cached-byte boundaries and overflow/consume prior cached bits in both byte orders.
CASES: Sequential readBits calls around the overflow boundary; verify returned values and retained cache alignment.
RISKS: readBits behavior for invalid counts, EOF, and close is not specified by the provided context.
RISKS: Expected input bytes and exact triggering call sequences are not provided; derive only from existing test context.