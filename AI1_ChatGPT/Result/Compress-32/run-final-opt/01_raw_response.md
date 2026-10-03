TARGETS: TarArchiveInputStream.getNextTarEntry parses TAR entry metadata, including gid values.
ORACLES: Trigger shouldReadBigGid; gid text "4294967294" must not cause NumberFormatException.
CASES: Read an entry whose gid is "4294967294" and verify next-entry retrieval succeeds.
CASES: Cover ordinary gid parsing and boundary values representable beyond signed int range.
RISKS: Available context does not state the expected TarArchiveEntry gid value or overflow policy.