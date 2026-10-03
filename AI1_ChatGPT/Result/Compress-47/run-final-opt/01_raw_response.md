TARGETS: getNextZipEntry(), canReadEntryData(ArchiveEntry), and read behavior for ZIP entries.  
ORACLES: Trigger test assertion; ZipArchiveEntry size/method metadata exposed after getNextZipEntry().  
CASES: Entry with unknown uncompressed size; assert it is marked unreadable via canReadEntryData.  
CASES: Known-size entry remains readable; compare behavior before/after reading entry data.  
CASES: Unknown size with stored vs deflated method if fixtures can encode both.  
RISKS: Context omits exact ZIP fixture construction and modified logic; derive expectations only from trigger/spec.