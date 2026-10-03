TARGETS: MultiValueMap custom serialization: private writeObject/readObject; empty and populated map round trips.
ORACLES: Trigger tests specify deserialization must not throw InvalidClassException for compatible serialized forms.
CASES: Serialize/deserialize empty MultiValueMap; verify deserialized map remains empty and usable.
CASES: Serialize/deserialize populated MultiValueMap; verify mappings, per-key collections, and totalSize survive.
RISKS: serialVersionUID/serialized-field compatibility is implicated; exact legacy stream fixture/source is unavailable.
RISKS: Do not infer behavior for malformed streams or non-serializable keys, values, maps, or factories.