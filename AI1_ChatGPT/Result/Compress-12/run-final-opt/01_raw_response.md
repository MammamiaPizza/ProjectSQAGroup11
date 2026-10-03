TARGETS: TarArchiveInputStream.getNextTarEntry() TAR header parsing, especially numeric mode-field decoding.
ORACLES: TarTestCase::testCOMPRESS178 and its reported rejection of `00\0 0765\0` at byte offset 2.
CASES: Read an entry with mode bytes `00\0 0765\0`; getNextTarEntry() should not throw IllegalArgumentException.
CASES: Verify parsed entry progression remains usable after the affected header is read.
RISKS: Context lacks the fixture contents and asserted entry metadata; avoid assuming a specific decoded mode value.