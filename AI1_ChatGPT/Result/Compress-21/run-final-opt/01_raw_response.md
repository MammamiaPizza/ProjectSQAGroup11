TARGETS: SevenZOutputFile finish/close header writing, especially writeFileEmptyStreams and writeBits.
TARGETS: putArchiveEntry, write, closeArchiveEntry for empty and non-empty SevenZArchiveEntry sequences.
ORACLES: Read produced archives with SevenZFile; entries/counts, names, and payload bytes must round-trip.
ORACLES: Archive creation/reading must not throw reported malformed-header/property/AIOOBE failures.
CASES: 6, 7, 8, and 9 all-empty entries; finish/close then read every entry.
CASES: 6, 7, 8, and 9 entries with a mix of empty and written byte content.
RISKS: Bit-set serialization boundary around counts 6–9 and empty-stream/file metadata encoding.
RISKS: Context lacks SevenZArchiveEntry construction/details and exact entry names/content conventions.