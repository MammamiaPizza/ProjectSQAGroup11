TARGETS: TarArchiveInputStream.getNextTarEntry(), getNextEntry(); parsing of octal header fields.
TARGETS: constructors (is), (is, blockSize), (is, blockSize, recordSize); static
matches(byte[],int).
ORACLES: TarTestCase.testCOMPRESS178 requires getNextTarEntry() to return an entry without
IllegalArgumentException.
ORACLES: parsed entry fields (name/size/mode) come from record bytes, including NUL-terminated octal
values.
CASES: normal space-padded octal fields; reserved tar record through the appropriate constructor.
CASES: assert no throw on numeric field '00{NUL}0765{NUL}'; all-zero, leading/trailing spaces/NULs.
CASES: truncated/short fields, multiple entries, read/available/getNextEntry boundaries.
RISKS: only TarArchiveInputStream signatures supplied; TarUtils/parseOctal internals and exact
numeric expectations not shown; avoid invented values.