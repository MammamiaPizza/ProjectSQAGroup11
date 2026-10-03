TARGETS: SimpleType.construct(Class) – IAE for Map/Collection/array; else valid type
TARGETS: SimpleType.constructUnsafe(Class) – bypass checks; handle Map/Collection/array
TARGETS: SimpleType._narrow(Class) – verify returned raw class equals subclass
TARGETS: SimpleType.withTypeHandler, withContentType, withStaticTyping – correct property mutation
ORACLES: Expected exceptions; compare getRawClass, getBindings, buildCanonicalName()
ORACLES: Deserialize POJO with "name" field using SimpleType-based type → no
UnrecognizedPropertyException
CASES: normal class, Map subclass (HashMap), Collection (ArrayList), array (int[]), inner class
CASES: class with generic parameters (e.g., TypeReference) – test bindings & canonical name
CASES: null argument to constructUnsafe – verify NullPointerException or graceful failure
RISKS: Bug might require integration with ObjectMapper; unit tests on SimpleType alone can't fully
reproduce