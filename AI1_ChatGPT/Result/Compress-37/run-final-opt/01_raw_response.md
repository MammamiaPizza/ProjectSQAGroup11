TARGETS: TarArchiveInputStream.getNextTarEntry handling PAX extended headers  
TARGETS: parsePaxHeaders(InputStream) record-length parsing and blank-line handling  
ORACLES: Trigger test survivesBlankLinesInPaxHeader must complete without NegativeArraySizeException  
ORACLES: Parsed valid PAX headers must still be applied to the following TarArchiveEntry  
CASES: PAX header containing a blank line before, between, or after valid header records  
CASES: Normal valid length-prefixed PAX key/value records followed by a regular entry  
CASES: Blank/empty PAX input must not create a negative-sized read buffer  
RISKS: Context lacks exact PAX malformed-input exception/termination expectations  
