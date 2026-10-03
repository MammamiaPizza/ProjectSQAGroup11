TARGETS: CpioArchiveOutputStream.putNextEntry, writeHeader, writeNewEntry, writeOldAsciiEntry,
closeArchiveEntry, finish, close
ORACLES: EOFException triggered in CpioTestCase.testCpioUnarchive; round-trip via
CpioArchiveInputStream; CpioArchiveEntry header fields preserved
CASES: write entries with varying sizes (0, 1, 4096, large), multiple entries in one archive, close
without entries, finish before close
RISKS: Cannot inspect CpioArchiveInputStream internals; bug likely in header padding or size
calculation causing premature EOF on read-back