TARGETS: getNextZipEntry, read, readStored, closeEntry, fill, pushback, readFirstLocalFileHeader
ORACLES: stored entry read bytes equal original; testReadingOfFirstStoredEntry expects first byte
100, not 0
CASES: normal stored entry sizes: 1, 100, 4096, >4096; stored after deflated; zero-length stored
entry
CASES: boundary: stored entry with data descriptor flag; entry size exactly BUFFER_SIZE; multiple
stored entries
CASES: error: reading after close, reading beyond entry, malformed local header
RISKS: target ZIP must be created via ZipArchiveOutputStream; data descriptor/Zip64 may interfere;
no bug root visible