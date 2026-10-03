TARGETS: UncheckedIOException(IOException cause), UncheckedIOException.ioException(),
CharacterReader.bufferUp(), CharacterReader.rangeEquals()
ORACLES: UncheckedIOException wraps IOException; ioException() returns original cause; bufferUp()
must throw UncheckedIOException when reader.read() throws IOException (e.g., due to binary data)
CASES: binary Reader returning non-text bytes, empty String input, null Reader, valid text,
boundary: readAheadLimit-sized buffer, EOF handling in rangeEquals
RISKS: No access to integration tests; must simulate binary I/O via custom Reader; only
UncheckedIOException and CharacterReader in scope