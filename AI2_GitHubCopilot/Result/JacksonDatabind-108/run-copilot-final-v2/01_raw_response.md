TARGETS: ObjectReader.readTree(JsonParser) for empty/non-empty input
TARGETS: ObjectReader.readTree(InputStream) with zero-length/empty stream
ORACLES: null when parser has no tokens (EOF), not MissingNode
ORACLES: Normal JsonNode when tokens exist
CASES: byte[0], "" JSON string, empty Reader; parser at EOF after init
CASES: parser pointing to valid JSON value after empty check
RISKS: _initForReading() may advance parser and lose "no token" state
RISKS: MissingNode vs null distinction may break existing clients
RISKS: Only JsonParser-based empty reads affected; other readTree overloads might differ