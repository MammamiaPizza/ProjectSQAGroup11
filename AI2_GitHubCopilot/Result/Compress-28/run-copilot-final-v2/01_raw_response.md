TARGETS: TarArchiveInputStream.getNextTarEntry(), read(), getRecord(); detect truncated entries.
ORACLES: IOException expected on truncated entry; based on shouldThrowAnExceptionOnTruncatedEntries
trigger test.
CASES: Partial file data (EOF before expected size), zero-length entry, entry ending at block
boundary, truncated long-name entry.
RISKS: Need binary tar fixtures; truncation vs. natural EOF may be ambiguous; only single test class
context given.