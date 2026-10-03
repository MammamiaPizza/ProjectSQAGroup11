TARGETS: DataUtil.readToByteBuffer(InputStream,int);
parseInputStream(InputStream,String,String,Parser); load(InputStream,...) empty-stream path.
ORACLES: empty (zero-byte) stream must yield empty ByteBuffer/Document, not
IOException/UncheckedIOException; ConnectTest::handlesEmptyStreamDuringParseRead expected
non-failure.
CASES: empty InputStream; empty + charsetName/baseUri; empty after BOM/partial read; maxSize=0, 1,
and > stream length; non-empty stream unchanged behavior.
RISKS: parseInputStream is package-private (test via load/readToByteBuffer); exact empty-document
charset/baseUri behavior not specified in supplied summary.