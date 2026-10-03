TARGETS: BasicDeserializerFactory._findRemappedType,
DeserializerCache._handleUnknownValueDeserializer, TypeDeserializerBase._defaultImpl handling,
SimpleAbstractTypeResolver.resolveAbstractType
ORACLES: For abstract→byte[] mapping, deserializer factory must produce array/primitive
deserializer, not BeanDeserializer
ORACLES: Expected test outcome: deserialization yields byte[] without "as a Bean"
JsonMappingException
CASES: Normal: abstract→ArrayList works; Boundary: abstract→byte[]; Edge: abstract→int[]; Error:
abstract→multi-dimensional array
RISKS: Missing full source → resolution chain inferred; regression on other array types unknown;
polymorphic interaction may add complexity