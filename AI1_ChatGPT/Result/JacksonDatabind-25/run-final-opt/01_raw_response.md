TARGETS: Array type remapping/deserializer lookup for byte[] when an abstract-type mapping/type override is active.
TARGETS: BasicDeserializerFactory remapped-type handling; DeserializerCache value-deserializer creation/caching.
TARGETS: TypeDeserializerBase default implementation/type-id resolution; SimpleAbstractTypeResolver mapping resolution.
ORACLES: Trigger must deserialize byte[] without “Can not deserialize Class [B ... as a Bean”.
ORACLES: Assert resulting value is a byte array with expected decoded contents from the trigger’s JSON/input.
CASES: Reproduce TestArrayDeserialization.testByteArrayTypeOverride890 using its existing mapper/module setup.
CASES: Normal byte[] deserialization without override; verify array remains byte[] and contents match.
CASES: Boundary empty byte[]/empty JSON array or string only if supported by existing trigger patterns.
RISKS: Context omits full method bodies and trigger source/input; derive assertions only from available test APIs/source.