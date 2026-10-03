TARGETS: getNextZipEntry() entry parsing (invalid/null sig handling), readFirstLocalFileHeader()
validation, closeEntry() drain logic
ORACLES: nested-entry count=1 from testListAllFilesWithNestedArchive, IOException expected from
testThrowOnInvalidEntry
CASES: nested zip with 1 inner entry → 1 returned; truncated/corrupt entry → IOException; empty
stream → null; DEFLATED/STORED normal paths
RISKS: truncated API in prompt; actual "invalid entry" detection logic (sig check/size mismatch) is
not fully visible; fix location uncertain