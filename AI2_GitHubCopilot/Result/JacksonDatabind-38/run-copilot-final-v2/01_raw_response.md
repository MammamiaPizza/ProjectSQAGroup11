TARGETS: CollectionType.construct, MapType.construct, SimpleType.construct (deprecated) & their
interaction
TARGETS: _narrow(Class<?>) in CollectionType, MapType, SimpleType; withStaticTyping methods
ORACLES: testExplicitMapType/CollectionType expect specific POJO class, not LinkedHashMap
ORACLES: SimpleType.construct throws IllegalArgumentException for Map/Collection/array inputs
CASES: Regular calls: construct(validClass, elemType), _narrow(sameClass), _narrow(subclass)
CASES: Boundary: withStaticTyping on instances; construct with null elemType; _narrow with unrelated
classes
CASES: Error: SimpleType.construct on HashMap, ArrayList, int[]; MapType.construct with invalid key
RISKS: Incomplete visibility into serialization/deserialization code; bug may lie outside supplied
API
RISKS: Potentially missing integration tests for type factory & deserialization pipeline
RISKS: Construction of SimpleType for classes extending Map/Collection might bypass checks