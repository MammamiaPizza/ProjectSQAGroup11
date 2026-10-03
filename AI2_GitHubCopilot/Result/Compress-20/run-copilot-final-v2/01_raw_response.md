TARGETS: CpioArchiveInputStream.readOldAsciiEntry/readOldBinaryEntry (mode parsing)
TARGETS: CpioArchiveEntry creation from parsed mode (0x1a4 from Redline RPM)
ORACLES: getNextCPIOEntry() returns non-null; read() does not throw
ORACLES: CpioArchiveEntry.getMode() equals the raw header mode value
CASES: normal: mode=0x1a4 (regular-file 0644), mode=0x41ED (dir 0755), mode=0x8000
CASES: boundary: mode=0 (no perms), mode=0xFFFF (max unsigned short), mode=0x81FF
CASES: error: mode with unknown type bits (e.g. 0x6000) but no IllegalArgumentException
RISKS: No Redline RPM binary provided; need to craft CPIO with exact header bytes for mode