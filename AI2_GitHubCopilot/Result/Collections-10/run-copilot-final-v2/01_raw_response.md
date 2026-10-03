TARGETS: MultiValueMap serialization roundtrip (writeObject/readObject) and post-deserialization
factory behavior.
ORACLES: Deserialized map equals original map using Map.equals; no InvalidClassException thrown;
getCollection returns same type.
ORACLES: After deserialization, put() should still create collections via the restored factory.
CASES: Empty map roundtrip; map with one key mapped to multiple values using default ArrayList
factory.
CASES: Map via decorate(map, HashSet.class) with multiple keys; map after putAll(Map) containing
multi-value entries.
CASES: Map with totalSize() boundary (0, 1, many); map with removeMapping then serialization.
RISKS: serialVersionUID may be absent; readObject may not restore the collectionFactory causing
future puts to fail.
RISKS: Compatibility tests may rely on serialized form from a reference version that is not present;
unknown null-handling.