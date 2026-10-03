TARGETS: putArchiveEntry, setAddPaxHeadersForNonAsciiNames, setLongFileMode(POSIX),
closeArchiveEntry, write
ORACLES: no IOException on non-ascii dir name; correct PaxHeaders.X size vs written bytes
CASES: non-ascii directory entry name; empty name; PAX + POSIX mode; nested non-ascii path
CASES: boundary non-ascii at name length; byte-len vs char-len accounting for header size
RISKS: encoding used may alter byte length; header byte size off-by-zero only in POSIX mode
RISKS: provided summary lacks full method bodies; expected sizes must be inferred from exception
only