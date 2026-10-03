TARGETS: TarArchiveOutputStream.finish(), putArchiveEntry(), closeArchiveEntry(), close()
ORACLES: Trigger expects finish to reject an archive with an unclosed entry ("After putArchive should follow closeArchive")
CASES: putArchiveEntry then finish without closeArchiveEntry: expect IOException/assertion failure
CASES: putArchiveEntry, closeArchiveEntry, then finish: expect successful completion
CASES: finish with no open entry: expect successful EOF/archive finalization
RISKS: Exact exception type/message and post-finish behavior are not provided; avoid asserting them