TARGETS: ZipArchiveInputStream.canReadEntryData must return false when uncompressed size is unknown
(e.g., data descriptor with size=0)
TARGETS: getNextZipEntry and CurrentEntry population of size, method, and general-purpose bit
influence canReadEntryData
ORACLES: COMPRESS-436 and test name "properlyMarksEntriesAsUnreadableIfUncompressedSizeIsUnknown"
indicate expected false for unknown-size entries
CASES: entry with data descriptor bit set, size=0, DEFLATED method → canReadEntryData should be
false (unreadable)
CASES: entry with known non‑zero size (e.g., 100 bytes), DEFLATED → canReadEntryData true (readable)
CASES: entry with known size via Zip64 extra field (large file) → readable; entry with size=-1
explicitly unknown → unreadable
CASES: entry with data descriptor but STORED method – may or may not be refused; test only against
DEFLATED if stream cannot detect data end
RISKS: exact condition (size==0 vs size==-1 vs gpFlag bit 3) not known; assume any unknown size
makes unreadable regardless of method
RISKS: creating ZipArchiveEntry with correct internal state requires using reflection/internal
fields to simulate local-header parsing