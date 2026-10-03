TARGETS: ObjectMapper default typing applied to JsonNode (TreeNode) deserialization, esp. arrays and
primitive values.
ORACLES: TestJsonNode::testArrayWithDefaultTyping must complete without JsonMappingException;
round-trip JsonNode equality.
CASES: deserialize empty [], [1,2,3], ["a","b"], [true,null], nested arrays, and ObjectNode fields
with default typing on.
CASES: boundary mixes of typed/untyped nodes, root array vs single value, int field causing
VALUE_NUMBER_INT-not-VALUE_STRING failure.
RISKS: only truncated signatures given; enableDefaultTyping/default typing config details are not
shown, so avoid inventing API names.
RISKS: cannot inspect fixed version or run tests; expected exact exception absence derives from
trigger failure message only.