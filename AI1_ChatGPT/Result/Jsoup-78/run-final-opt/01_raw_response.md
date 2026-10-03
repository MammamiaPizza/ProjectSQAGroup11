TARGETS: DataUtil.load(InputStream,...), parseInputStream, and readToByteBuffer handling empty streams  
ORACLES: Trigger expects no UncheckedIOException when parsing an empty response stream  
CASES: Empty InputStream through DataUtil.load with charset/baseUri/parser path  
CASES: Empty InputStream through readToByteBuffer; verify successful empty ByteBuffer result  
CASES: Normal nonempty stream remains readable and parsed without regression  
RISKS: Need package access for parseInputStream/readToByteBuffer overloads; public load is primary path  
RISKS: Exact empty Document content is not specified here; avoid asserting invented HTML structure