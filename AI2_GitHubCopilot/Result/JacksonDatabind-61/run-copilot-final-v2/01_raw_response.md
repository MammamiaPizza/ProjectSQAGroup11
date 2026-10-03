TARGETS: ObjectMapper.enableDefaultTyping(); StdTypeResolverBuilder.useForType() handles
primitives/wrappers
ORACLES: Existing test testDefaultTypingWithLong passes (no JsonMappingException); Long→long subtype
allowed
CASES: Normal: Long value in long field; wrapper for int, boolean, double; boundary: null, array of
primitives
CASES: Error: non-matching type id; type id for wrapper but field is primitive; unknown class name
RISKS: Full useForType logic not shown; defaultTyping rules for primitive vs wrapper inferred from
bug scenario only