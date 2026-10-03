TARGETS: CompressorStreamFactory.createCompressorInputStream(InputStream) signature detection, including DEFLATE.  
TARGETS: DeflateCompressorInputStream read/read(byte[],off,len), skip, available, close behavior.  
ORACLES: Existing DetectCompressorTestCase::testDetection and CompressorException indicate detection result/failure.  
CASES: Detect a valid DEFLATE-compressed byte stream via factory; expect a Deflate compressor stream, not CompressorException.  
CASES: Read detected DEFLATE stream to EOF and verify decompressed bytes match original input.  
CASES: Boundary reads: single-byte, buffer reads, EOF; exercise skip/available if supported by fixture.  
CASES: Invalid or unrecognized signature should retain CompressorException behavior.  
RISKS: No explicit DEFLATE signature format or expected bytes are provided; derive fixtures from public compressor APIs only.