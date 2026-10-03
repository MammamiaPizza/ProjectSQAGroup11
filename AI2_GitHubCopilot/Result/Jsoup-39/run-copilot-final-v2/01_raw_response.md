TARGETS: DataUtil.parseByteData reading from ByteBuffer with UTF-8 BOM + no charset,
DataUtil.load(InputStream,...), DataUtil.readToByteBuffer
ORACLES: test discardsSpuriousByteOrderMarkWhenNoCharsetSet expects "One" after BOM removal; BOM
should be stripped before parsing starts
CASES: stream with UTF-8 BOM, no charset header, content "One"; BOM + charset in meta; BOM only
(empty content); no BOM; partial BOM
RISKS: cannot inspect parser internals; test may fail due to buffer position or charset detection
mis-handling BOM bytes