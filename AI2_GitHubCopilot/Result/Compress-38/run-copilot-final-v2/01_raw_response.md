TARGETS:  TarArchiveEntry(byte[]), parseTarHeader, evaluateType, isDirectory, getName
ORACLES:  No IOException; isDirectory() returns true for names ending '/'; PAX header intact
CASES:    Name ending '/', dir-type flag; name = "/"; name = "foo/" non-dir flag; name normal file
RISKS:    Cannot replicate full TarArchiveInputStream PAX pipeline; depends on TarUtils and encoding