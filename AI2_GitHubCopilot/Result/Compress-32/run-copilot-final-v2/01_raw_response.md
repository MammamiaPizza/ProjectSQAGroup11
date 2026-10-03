TARGETS: getNextTarEntry() parsing of octal numeric header fields (uid, gid, size).
TARGETS: Internal methods that convert tar header numeric strings to long (likely parseOctal).
ORACLES: TarArchiveEntry.getLongGroupId() / getLongUserId() return expected long values without
truncation.
ORACLES: No NumberFormatException from parsing values up to 0xFFFFFFFF (e.g., 4294967294).
CASES: Normal UID/GID (0, 1000) → entry fields correct.
CASES: Large GID 4294967294 → entry groupId long value exact, no exception.
CASES: Boundary: GID = Integer.MAX_VALUE + 1, Long.MAX_VALUE, 0x1FFFFFFFF, entirely numeric octals.
CASES: Empty/dash-filled numeric field → default 0 without error.
RISKS: Cannot inspect internal parse method signatures; rely on public TarArchiveEntry long
accessors.
RISKS: Must create mock tar input stream with test headers containing large numeric fields.