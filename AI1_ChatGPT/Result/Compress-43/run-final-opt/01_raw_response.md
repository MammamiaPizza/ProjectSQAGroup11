TARGETS: addRawArchiveEntry(ZipArchiveEntry, InputStream); local-header general-purpose flags/data descriptor use
ORACLES: DataDescriptorTest::doesntWriteDataDescriptorWhenAddingRawEntries byte-array expected value at index 0
CASES: raw DEFLATED entry with known CRC, compressed size, and size; verify no data-descriptor flag/output
CASES: raw entry lacking required known metadata, if API permits; distinguish descriptor-required behavior
RISKS: provided context omits full test setup, exact byte layout, and addRawArchiveEntry implementation details