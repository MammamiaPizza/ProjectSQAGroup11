TARGETS: CompressorStreamFactory.createCompressorInputStream(InputStream) auto-detection of raw
deflate streams
TARGETS: DeflateCompressorInputStream construction and read behavior for raw deflate input
ORACLES: No CompressorException for valid deflate signature; returned type is
DeflateCompressorInputStream
ORACLES: Existing DetectCompressorTestCase.testDetection should pass for all formats including
deflate
CASES: Provide InputStream with raw deflate compressed data (no header); verify factory returns
DeflateCompressorInputStream
CASES: Boundary: empty stream, stream with only 1 byte, stream truncated mid-header
CASES: Error: stream with unknown magic bytes; expect CompressorException with message
RISKS: Raw deflate lacks distinguishing magic; detection may use trial decompression, which could
succeed on some arbitrary bytes
RISKS: Detection order may affect outcome; if earlier format signatures mimic deflate start, false
match may occur
RISKS: Internal detection logic not directly testable; only black-box via public
createCompressorInputStream