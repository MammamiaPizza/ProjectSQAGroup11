TARGETS: ZipArchiveInputStream.getNextZipEntry(), read(), and nested ZIP entry traversal.
ORACLES: Existing trigger expectations: nested archive lists 1 file; invalid entry causes IOException.
CASES: ZIP containing nested archive; obtain outer entry, read its bytes, then traverse nested input.
CASES: Invalid local-file-header/entry data passed to getNextZipEntry() must throw IOException.
CASES: Valid ZIP entry remains readable and next-entry iteration preserves normal behavior.
RISKS: Context omits exact malformed bytes and full modified logic; derive fixtures only from public ZIP format/tests.