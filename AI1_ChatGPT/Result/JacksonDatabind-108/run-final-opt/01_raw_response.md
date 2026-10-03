TARGETS: ObjectReader tree reads using JsonParser at EOF, especially readTree(JsonParser).
ORACLES: Trigger expects null; EOF parser must not yield MissingNode.
CASES: Empty input parser passed to ObjectMapper.reader().readTree(parser) returns null.
CASES: Empty input parser passed to ObjectMapper.readerFor(JsonNode.class).readTree(parser) returns null.
CASES: Non-empty parser/tree read still returns the parsed JsonNode.
RISKS: API overload details are truncated; focus on the trigger's parser-and-reader path.