TARGETS: createFile(String), createValue(String, Class<?>) for File/FileInputStream types
ORACLES: testExistingFilePattern expects FileInputStream from createValue;
testExistingFilePatternFileNotExist expects null for non-existing file
CASES: existing file -> FileInputStream; non-existing file -> null; empty/null string; boundary:
valid filename with no read permission
RISKS: Only file-type handling exhibited bug; other TypeHandler methods (Date, URL, Number) likely
unchanged but untested here