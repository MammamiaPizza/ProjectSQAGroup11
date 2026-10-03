TARGETS: MultiKey constructors, equals(Object), hashCode(), getKeys(), getKey(int), size() after serialization
ORACLES: Existing trigger TestMultiKey::testEqualsAfterSerialization expects deserialized key to retrieve value 2
ORACLES: equals/hashCode contract and serialized/deserialized object equivalence
CASES: Construct 2-key MultiKey, serialize/deserialize, assert equality and identical hashCode
CASES: Use original and deserialized MultiKey interchangeably as map keys; lookup must return stored value
CASES: Normal key counts 2 through 5; verify size and indexed getKey results
CASES: Object[] constructor clone/non-clone behavior only if existing tests establish expected aliasing
RISKS: Hash state may not be restored after deserialization, breaking hash-based lookup despite equals
RISKS: Context lacks serialization helper and broader constructor/null/index error expectations