TARGETS: CpioArchiveOutputStream.putNextEntry, writeHeader/writeNewEntry/writeOldAsciiEntry, closeArchiveEntry, finish  
ORACLES: CpioTestCase.testCpioUnarchive must read produced CPIO without EOFException  
CASES: Create archive entries and payloads, close each entry, finish/close, then unarchive and verify readable contents  
CASES: Boundary payload/name lengths requiring CPIO header/data padding and consecutive entries  
CASES: Empty entry and final trailer/finish behavior; verify archive reader reaches normal end, not EOFException  
RISKS: Format-specific behavior exists (new vs old ASCII); supplied context does not identify affected format or exact bytes  
RISKS: APIs/details for CpioArchiveEntry construction and reader assertions are not provided; avoid inventing them