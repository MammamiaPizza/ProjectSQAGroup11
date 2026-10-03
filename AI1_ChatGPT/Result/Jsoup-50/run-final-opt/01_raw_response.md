TARGETS: DataUtil.load(File/InputStream,...), readFileToByteBuffer, readToByteBuffer, parseByteData BOM handling  
ORACLES: Document charset/text assertions from DataUtilTest::supportsBOMinFiles and parsed document content  
CASES: UTF-8 BOM file with null/unspecified charset should decode BOM-prefixed content correctly  
CASES: BOM input via File and InputStream; verify BOM bytes do not appear as document text  
CASES: Explicit charset versus BOM, empty input, and non-BOM UTF-8 input boundaries  
RISKS: Available context does not state expected precedence when explicit charset conflicts with BOM  
RISKS: Do not derive expectations from another program version or unlisted DataUtil behavior