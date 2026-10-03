TARGETS: ObjectMapper default-typing configuration and JsonNode/tree deserialization paths.
ORACLES: Trigger expects typed array/tree content to accept integer elements, not require a String type id.
CASES: Deserialize a default-typed JSON array containing integer values into JsonNode/tree target.
CASES: Verify array node element count and integer value after default-typing read.
CASES: Cover nested arrays or object fields containing JsonNode arrays with numeric elements.
RISKS: Type-id handling may still be required for non-tree polymorphic values; avoid asserting unrelated typing modes.
RISKS: Context omits exact default-typing API invocation and full trigger source.