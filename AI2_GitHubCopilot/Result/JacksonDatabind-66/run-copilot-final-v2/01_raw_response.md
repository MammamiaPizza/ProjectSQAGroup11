TARGETS: StdKeyDeserializer.forType must return a key deserializer capable of handling enum subtypes
for polymorphic enums.
TARGETS: StdKeyDeserializer.EnumKD._parse must resolve keys against all concrete enum constants in
the hierarchy, not just the declared type.
TARGETS: DelegatingKD may be used to delegate to a BeanDeserializer for polymorphic enum key
resolution when type info is present.
ORACLES: Polymorphic enum key "FOO" maps to correct subtype enum constant SubEnum.FOO; unknown keys
throw InvalidFormatException.
ORACLES: Normal single-type enum key deserialization remains unchanged; @JsonValue-based key strings
still resolve correctly.
CASES: Simple enum key deserialization; polymorphic enum key with/without type prefix; unknown key;
null/empty key.
CASES: Enum key where the same name exists in multiple subtypes — must use type id to disambiguate.
CASES: Enum key that uses a custom @JsonValue or toString representation for mapping.
RISKS: Test must replicate trigger-test enum hierarchy (SuperTypeEnum with @JsonSubTypes); need
deserialization context with valid annotations.