TARGETS: JsonGenerator.writeEmbeddedObject, canWriteBinaryNatively, writeBinary
ORACLES: Tests expect no JsonGenerationException; output is valid base64 string
CASES: Normal binary, empty, single-byte, large, offset/length boundaries, null
RISKS: Fallback to base64 encoding may need to respect generator context (startObject/endObject)