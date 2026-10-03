TARGETS: addRawArchiveEntry, closeArchiveEntry, closeCopiedEntry, usesDataDescriptor, preClose
ORACLES: DataDescriptorTest.doesntWriteDataDescriptorWhenAddingRawEntries expects byte[0]==0 (no
descriptor)
CASES: raw entry with CRC/SIZE known → no descriptor written; raw entry CRC_UNKNOWN → should write;
entry with Zip64 fallback?
RISKS: truncated API hides phased/copied logic; zip64/data-descriptor interplay unclear; test only
checks first byte