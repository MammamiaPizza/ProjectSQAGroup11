TARGETS: ObjectMapper default-typing configuration and read/write of primitive long fields through polymorphic containers.  
TARGETS: StdTypeResolverBuilder type serializer/deserializer selection for primitive vs wrapper JavaType.  
ORACLES: Trigger expectation: Long type id must deserialize into declared primitive long without JsonMappingException.  
CASES: Reproduce Data.key declared long inside HashMap entry (longAsField) with default typing enabled.  
CASES: Round-trip primitive long values including 0, negative, and Long.MIN_VALUE/MAX_VALUE if trigger fixture permits.  
CASES: Compare direct primitive-long property round-trip versus container-held Data instance.  
RISKS: Available context omits exact DefaultTyping mode, Data fixture definition, and serialized JSON/type-id format.