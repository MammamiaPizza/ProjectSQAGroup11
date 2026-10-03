TARGETS: TypeFactory.constructSpecializedType, _mapType, findTypeParameters for Properties.class
ORACLES: TestMapConversions expects property "x"=129(String); TestMapDeserialization expects
123(String)
ORACLES: TestTypeFactory expects Properties value type is String.class, not Object.class
CASES: constructType(Properties.class) -> key/value types must both resolve to String.class
CASES: constructSpecializedType with Properties subclass (boundary) and with raw Properties
CASES: round-trip: serialize Properties, deserialize back, check non-null integer-as-string values
RISKS: Properties extends Hashtable<Object,Object> but findTypeParameters may return Object, Object
RISKS: constructSpecializedType currently checks Map/Collection isAssignable but not special-casing
Properties
RISKS: Limited visibility of test source; exact JSON input format for
testMapToProperties/testReadProperties unknown